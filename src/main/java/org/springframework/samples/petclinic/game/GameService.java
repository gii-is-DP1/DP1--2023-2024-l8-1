package org.springframework.samples.petclinic.game;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.samples.petclinic.player.PlayerRol;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

@Service
public class GameService {

    GameRepository repo;
    UserService userService;
    PlayerService playerService;
    ShipService shipService;
    HexService hexService;
    CardService cardService;

    @Autowired
    public GameService(GameRepository repo, UserService userService, PlayerService playerService,
            ShipService shipService, HexService hexService, CardService cardService) {
        this.repo = repo;
        this.userService = userService;
        this.playerService = playerService;
        this.shipService = shipService;
        this.hexService = hexService;
        this.cardService = cardService;
    }

    @Transactional(readOnly = true)
    List<Game> getGames() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Game> getPublicas() {
        List<Game> result = repo.findPublicas();
        return result;
    }

    @Transactional(readOnly = true)
    public Game getById(int id) {
        Optional<Game> result = repo.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    @Transactional(readOnly = true)
    public Game findByName(String name) {
        Optional<Game> result = repo.findByName(name);
        return result.isPresent() ? result.get() : null;
    }

    @Transactional(readOnly = true)
    public List<Player> findGamePlayers(String name) throws AccessDeniedException {
        return repo.findGamePlayers(findByName(name).getId());
    }

    @Transactional
    public Game createGame(@Valid Game newGame) {
        Player host = userService.findPlayerByUser(userService.findCurrentUser().getId());
        host.setRol(PlayerRol.HOST);
        newGame.setHost(host);
        return repo.save(newGame);
    }

    @Transactional
    public Game saveGame(@Valid Game newGame) {
        return repo.save(newGame);
    }

    @Transactional
    public Game updateGame(Game game, int id) {
        Game toUpdate = getById(id);
        BeanUtils.copyProperties(game, toUpdate, "id");
        return saveGame(toUpdate);
    }

    @Transactional
    public Game startGame(String name) throws BadRequestException {
        Game game = findByName(name);
        if (game.getPlayers().size() != 2) {
            throw new BadRequestException("La sala debe estar completa antes de empezar la partida");
        } else {
            generateShipInGame(game);
            generateCardsInGame(game);
            game.setState(GameState.START_PLAYER_CHOICE);
        }
        return updateGame(game, game.getId());
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    private void generateShipInGame(Game game) {
        try {
            Player player1 = game.getPlayers().get(0);
            Player player2 = game.getPlayers().get(1);
            Player host = game.getHost();
            shipService.genShipsForOnePlayer(player1.getId());
            shipService.genShipsForOnePlayer(player2.getId());
            shipService.genShipsForOnePlayer(host.getId());
        } catch (Exception e) {
            System.out.println("Error during ship generation in the game");
            throw new RuntimeException("Error during ship generation in the game: " + game.getName(), e);
        }
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    private void generateCardsInGame(Game game) {
        try {
            Player player1 = game.getPlayers().get(0);
            Player player2 = game.getPlayers().get(1);
            Player host = game.getHost();
            cardService.genCardsForOnePlayer(player1.getId());
            cardService.genCardsForOnePlayer(player2.getId());
            cardService.genCardsForOnePlayer(host.getId());
        } catch (Exception e) {
            System.out.println("Error during card generation in the game");
            throw new RuntimeException("Error during card generation in the game: " + game.getName(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Ship> getShipsOfGame(String name){
        try {
            Game game = findByName(name);
            Player player1 = game.getPlayers().get(0);
            Player player2 = game.getPlayers().get(1);
            Player host = game.getHost();
            List<Ship> aux = shipService.findAllShips();
            Set<Integer> ids = Set.of(player1.getId(), player2.getId(), host.getId());
            aux.stream().map(s -> ids.contains(s.getPlayer().getId()));
            return aux;
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo las naves de la partida: " + name);
        }
    }

    @Transactional
    public Game joinPlayer(String name) {
        Game toUpdate = findByName(name);
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Player> aux = toUpdate.getPlayers();
        aux.add(me);
        toUpdate.setPlayers(aux);
        me.setRol(PlayerRol.GUEST);
        playerService.updatePlayer(me, me.getId());
        return updateGame(toUpdate, toUpdate.getId());

    }

    @Transactional
    public Game kickPlayer(String name, int id) {
        Game toUpdate = findByName(name);
        List<Player> aux = toUpdate.getPlayers();
        aux.remove(playerService.findPlayerById(id));
        toUpdate.setPlayers(aux);
        return updateGame(toUpdate, toUpdate.getId());

    }

    @Transactional
    public void deleteGameById(int id) {
        Game toDelete = getById(id);
        repo.delete(toDelete);
    }

    @Transactional
    public void setUpShips(String name, Hex hex) {
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Ship> playerShips = shipService.selectShipsFromSupply(me.getId());
        Ship shipInHex = playerShips.get(0);
        shipInHex.setHex(hex);
        shipInHex.setState(ShipState.ON_GAME);
        shipService.updateShip(shipInHex, shipInHex.getId());
        hex.setOccuped(true);
        hexService.updateHex(hex, hex.getId());
    }
}
