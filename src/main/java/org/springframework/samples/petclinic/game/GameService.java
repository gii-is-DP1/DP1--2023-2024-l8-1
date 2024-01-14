package org.springframework.samples.petclinic.game;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.gameboard.GameBoard;
import org.springframework.samples.petclinic.gameboard.GameBoardService;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.round.RoundService;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnService;
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
    RoundService roundService;
    PhaseService phaseService;
    TurnService turnService;
    ShipService shipService;
    HexService hexService;
    CardService cardService;

    @Autowired
    public GameService(GameRepository repo, UserService userService, PlayerService playerService,
            RoundService roundService, PhaseService phaseService, TurnService turnService,
            ShipService shipService, HexService hexService, CardService cardService) {
        this.repo = repo;
        this.userService = userService;
        this.playerService = playerService;
        this.roundService = roundService;
        this.phaseService = phaseService;
        this.turnService = turnService;
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
        Game game = findByName(name);
        List<Player> players = new ArrayList<>();
        players.add(game.getHost());
        for (Player player : game.getPlayers()) {
            players.add(player);
        }
        return players;
    }

    @Transactional(readOnly = true)
    public List<Game> findCurrentPlayerUserGames() {
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Game> gamesInDB = repo.findAll();
        List<Game> playerGames = gamesInDB.stream().filter(g -> g.getHost().equals(me) || g.getPlayers().contains(me)).toList();
        return playerGames;
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
    public Round addRound(Game game, Boolean isInitial, Boolean isFinal) {
        List<Round> rounds = game.getRounds() != null ? game.getRounds() : new ArrayList<>();
        RoundBuilder builder = new RoundBuilder(roundService, phaseService, turnService, cardService);
        if (isInitial) {
            Director director = new Director(builder, findGamePlayers(game.getName()), 0);
            director.InitialRound();
        } else {
            Round round = game.getRounds().get(game.getRounds().size() - 1);
            Player player = (round.getPhases().get(0)).getTurns().get(0).getPlayer();
            int playerInicial = findGamePlayers(game.getName()).indexOf(player) + 1 > 2
                    ? findGamePlayers(game.getName()).indexOf(player) + 1 - 3
                    : findGamePlayers(game.getName()).indexOf(player) + 1;
            if (isFinal) {
                Director director = new Director(builder, findGamePlayers(game.getName()), playerInicial);
                director.FinalRound();
            } else {
                Director director = new Director(builder, findGamePlayers(game.getName()), playerInicial);
                director.NormalRound();
            }
        }
        Round round = builder.getRound();
        rounds.add(round);
        game.setRounds(rounds);
        saveGame(game);
        return round;
    }

    @Transactional
    public Game startGame(String name) throws BadRequestException {
        Game game = findByName(name);

        addRound(game, true, false);
        generateShipInGame(game);
        generateCardsInGame(game);

        game.setState(GameState.START_PLAYER_CHOICE);
        return saveGame(game);
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
    public List<Ship> getShipsOfGame(String name) {
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
    public Turn getCurrentTurn(Game game) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        return turn;
    }

    @Transactional
    public Phase getCurrentPhase(Game game) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        return phase;
    }

    @Transactional
    public void skipTurn(Game game, Player player) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();

        if (round != game.getRounds().get(0)) {
            if (turn.getPlayer() == player) {
                turn.setIsOver(true);
                turnService.saveTurn(turn);
                roundService.roundIsOver(round, phase, game);
            } else {
                throw new AccessDeniedException("No es tu turno.");
            }
        } else {
            throw new AccessDeniedException("No se puede pasar en la ronda inicial.");
        }
    }

    @Transactional
    public Game initialRound(String name, int sector, int hexPosition, Player player) {
        Game game = findByName(name);
        Round round = game.getRounds().get(0);
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        if (!phase.getIsOrder()) {
            if (turn.getPlayer() == player) {
                if (!game.getGameBoard().getSectors().get(sector).getIsTriPrime()) {
                    if (!game.getGameBoard().getSectors().get(sector).getHexs().stream()
                            .anyMatch(s -> s.getOccuped())) {
                        Hex hex = game.getGameBoard().getSectors().get(sector).getHexs().get(hexPosition - 7 * sector);
                        Ship ship = (shipService.selectShipsFromSupply(player.getId())).get(0);
                        ship.setHex(hex);
                        ship.setState(ShipState.ON_GAME);
                        hex.setOccuped(true);
                        turn.setIsOver(true);
                        shipService.save(ship);
                        hexService.save(hex);
                        turnService.saveTurn(turn);
                    } else {
                        throw new AccessDeniedException("El sector debe estar vacio.");
                    }
                } else {
                    throw new AccessDeniedException("No puedes elegir el sector TriPrime en la ronda inicial");
                }
            } else {
                throw new AccessDeniedException("No es tu turno.");
            }
        } else {
            throw new AccessDeniedException("Es hora de ordenar las cartas.");
        }
        roundService.roundIsOver(round, phase, game);
        if (game.getRounds().get(0).getIsOver()) {
            game.setState(GameState.IN_PROGRESS);
            addRound(game, false, false);
        }
        return saveGame(game);
    }

    @Transactional
    public void orderCards(Game game, Player player) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        if (phase.getIsOrder()) {
            if (turn.getPlayer() == player) {
                turn.setIsOver(true);
                turnService.saveTurn(turn);
            } else {
                throw new AccessDeniedException("No es tu turno.");
            }
        } else {
            throw new AccessDeniedException("No es hora de ordenar las cartas.");
        }
        roundService.roundIsOver(round, phase, game);
        if (round == game.getRounds().get(0) && round.getIsOver()) {
            game.setState(GameState.IN_PROGRESS);
            addRound(game, false, false);
        } else if (round.getIsOver()) {
            addRound(game, false, false);
        }

    }

    @Transactional
    public Game setHex(String name, int sector, int hexPosition, Player player) {
        Game game = findByName(name);
        Round round = new Round();
        if (game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get() == null) {
            round = addRound(game, false, false);
        } else {
            round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        }

        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();

        return game;
    }

    @Transactional
    public Game pointPhase(String name, int sector, Player player) {
        Game game = findByName(name);
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver() && s.getIsPoint()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Sector sectorSelected = game.getGameBoard().getSectors().get(sector);
        if (turn.getPlayer() == player) {
            if (game.getGameBoard().getSectors().get(sector).getHexs().stream().anyMatch(s -> s.getOccuped())) {
                int points = 0;
                for (Hex hex : sectorSelected.getHexs()) {
                    if (hex.getOccuped() && hex.getShips().get(0).getPlayer() == player) {
                        points += hex.getPuntos();
                    }
                }
                player.setScore(player.getScore() + points);
                playerService.savePlayer(player);
                turn.setIsOver(true);
                turnService.saveTurn(turn);
            } else {
                throw new AccessDeniedException("El sector debe estar ocupado.");
            }
        } else {
            throw new AccessDeniedException("No es tu turno.");
        }
        roundService.roundIsOver(round, phase, game);
        if (phase.getIsOver())
            limpiarExtras(game);
        return saveGame(game);
    }

    @Transactional
    public String getCurrentAction(Game game, Player player) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        if (turn.getPlayer() == player) {
            CardType tipo = player.getCards().get(round.getPhases().indexOf(phase)).getType();
            return tipo.toString();
        } else {
            throw new AccessDeniedException("No es tu turno.");
        }
    }

    @Transactional
    public void limpiarExtras(Game game) {
        GameBoard tablero = game.getGameBoard();
        for (Sector sector : tablero.getSectors()) {
            for (Hex hex : sector.getHexs()) {
                if (hex.getShips().size() > hex.getPuntos() + 1) {
                    for (int i = 0; i < hex.getShips().size() - hex.getPuntos() + 1; i++) {
                        List<Ship> ships = hex.getShips();
                        ships.get(i).setState(ShipState.IN_SUPPLY);
                        ships.get(i).setHex(null);
                        ships.remove(i);
                        hex.setShips(ships);
                        shipService.save(ships.get(i));
                        hexService.save(hex);
                    }
                }
            }
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
