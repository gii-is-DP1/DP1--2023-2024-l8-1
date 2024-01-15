package org.springframework.samples.petclinic.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.exceptions.NotOwnedHex;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.exceptions.YouCannotPlay;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRol;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/game")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Games", description = "The Game managemet API")
public class GameRestController {

    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public GameRestController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> findAll() {
        return new ResponseEntity<>((List<Game>) gameService.getGames(), HttpStatus.OK);
    }

    @GetMapping("/publicas")
    public ResponseEntity<List<Game>> publicGames() {
        return new ResponseEntity<>((List<Game>) gameService.getPublicas(), HttpStatus.OK);
    }

    @GetMapping("/friendGames")
    public ResponseEntity<List<Game>> friendGames() {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        return new ResponseEntity<>((List<Game>) gameService.getFriendGames(aux), HttpStatus.OK);
    }

    @GetMapping("/playerCurrentGames")
    public ResponseEntity<List<Game>> playerCurrentGames() {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        return new ResponseEntity<>((List<Game>) gameService.getCurrentPlayerGames(aux), HttpStatus.OK);
    }

    @GetMapping("/playerGames")
    public ResponseEntity<List<Game>> playerGames() {
        return new ResponseEntity<>((List<Game>) gameService.findPlayerUserGames(), HttpStatus.OK);
    }

    @GetMapping("/play/{name}")
    public ResponseEntity<Game> findGameByName(@PathVariable("name") String name) {
        Game gameToGet = gameService.findByName(name);
        if (gameToGet == null)
            throw new ResourceNotFoundException("Game with name " + name + "not found!");
        return new ResponseEntity<Game>(gameToGet, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> findGame(@PathVariable("id") int id) {
        Game gameToGet = gameService.getById(id);
        if (gameToGet == null)
            throw new ResourceNotFoundException("Game with id " + id + "not found!");
        return new ResponseEntity<Game>(gameToGet, HttpStatus.OK);
    }

    @GetMapping("/lobby/{name}")
    public ResponseEntity<List<Player>> findGamePlayers(@PathVariable String name) {
        Game gameToGet = gameService.findByName(name);
        if (gameToGet == null)
            throw new ResourceNotFoundException("Game with name " + name + "not found!");
        List<Player> gamePlayers = gameService.findGamePlayers(name);
        return new ResponseEntity<List<Player>>(gamePlayers, HttpStatus.OK);
    }

    @GetMapping("/getWinner/{name}")
    public ResponseEntity<List<Player>> findSortedGamePlayers(@PathVariable("name") String name) {
        List<Player> ls = findGamePlayers(name).getBody();
        ls.sort(Comparator.comparing(Player::getScore).reversed());
        return new ResponseEntity<List<Player>>(ls, HttpStatus.OK);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> crateGame(@RequestBody @Valid Game newGame, BindingResult br) {
        Game result = null;
        if (!br.hasErrors()) {
            result = gameService.createGame(newGame);
        } else
            throw new BadRequestException(br.getAllErrors());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updateGame(@RequestBody @Valid Game newGame, BindingResult br,
            @PathVariable("id") int id) {
        Game gameToUpdate = this.findGame(id).getBody();
        if (br.hasErrors())
            throw new BadRequestException(br.getAllErrors());
        else if (newGame.getId() == null || !newGame.getId().equals(id))
            throw new BadRequestException("Achievement id is not consistent with resource URL:" + id);
        else {
            BeanUtils.copyProperties(newGame, gameToUpdate, "id");
            gameService.saveGame(gameToUpdate);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/start/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> startGame(@PathVariable("name") String name) {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        Game game = gameService.findByName(name);
        if (aux != game.getHost()) {
            throw new AccessDeniedException("No puedes empezar la partida si no eres el host de la partida");
        } else if (game.getPlayers().size() != 2) {
            throw new BadRequestException("La sala debe estar completa antes de empezar la partida");
        } else if(game.state == GameState.LOBBY){
            gameService.startGame(name);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/setHex/{name}/{sector}/{hexPosition}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> setHex(@PathVariable("name") String name,
            @PathVariable("sector") int sector, @PathVariable("hexPosition") int hexPosition) {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        Game game = gameService.findByName(name);
        if (aux.getRol() == PlayerRol.SPECTATOR) {
            throw new AccessDeniedException("Estás viendo la partida en modo espectador, no puedes jugar.");
        } else {
            if (!game.getRounds().get(0).getIsOver()) {
                try {
                    gameService.initialRound(name, sector, hexPosition, aux);
                } catch (AccessDeniedException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCause().getMessage());
                }
            } else {
                if (game.getRounds().stream().filter(r -> !r.getIsOver()).findFirst().get().getPhases().stream()
                        .filter(p -> !p.getIsOver()).findFirst().get().getIsPoint()) {
                    gameService.pointPhase(game, sector, aux);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/skipTurn/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> skipTurn(@PathVariable("name") String name) {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        Game game = gameService.findByName(name);
        if (aux.getRol() == PlayerRol.SPECTATOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Estás viendo la partida en modo espectador, no puedes jugar.");
        } else {
            try {
                gameService.skipTurn(game, aux);
            } catch (AccessDeniedException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCause().getMessage());
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/isInitial/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isInitial(@PathVariable("name") String name) {
        Game game = gameService.findByName(name);
        Boolean res = game.getRounds().get(0) == game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst()
                .get();
        return res;
    }

    @GetMapping("/getCurrentTurn/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> getCurrentTurn(@PathVariable("name") String name) {
        Game game = gameService.findByName(name);
        List<String> ls = List.of(gameService.getCurrentTurn(game).getPlayer().getUser().getUsername());
        return new ResponseEntity<List<String>>(ls, HttpStatus.OK);
    }

    @GetMapping("/getCurrentPhase/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Phase> getCurrentPhase(@PathVariable("name") String name) {
        Game game = gameService.findByName(name);
        return new ResponseEntity<Phase>(gameService.getCurrentPhase(game), HttpStatus.OK);
    }

    @PutMapping("/setOrder/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> setOrder(@PathVariable("name") String name) {
        try {
            Game game = gameService.findByName(name);
            Player player = userService.findPlayerByUser(userService.findCurrentUser().getId());
        if (player.getRol() == PlayerRol.SPECTATOR) {
            throw new AccessDeniedException("Estás viendo la partida en modo espectador, no puedes jugar.");
        } else {
                gameService.orderCards(game, player);
        }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAction/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> getCurrentAction(@PathVariable("name") String name) {
        Game game = gameService.findByName(name);
        List<String> ls = new ArrayList<>();
        if (game.getRounds().get(0) != game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get()
                && !getCurrentPhase(name).getBody().getIsOrder() && !getCurrentPhase(name).getBody().getIsPoint()) {
            ls.add(gameService.getCurrentAction(game).getType().toString());
        } else {
            ls.add("nada");
        }
        return new ResponseEntity<List<String>>(ls, HttpStatus.OK);
    }

    @PutMapping("/join/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> joinGame(@PathVariable("name") String name) {
        gameService.joinPlayer(name);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/lobby/{name}/{id}")
    public ResponseEntity<Game> kickPlayer(@PathVariable("name") String name, @PathVariable("id") int id) {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        if (aux.getRol() != PlayerRol.HOST) {
            throw new AccessDeniedException("No puedes echar a un jugador si no eres el host de la partida");
        } else {
            findGame(gameService.findByName(name).getId());
            gameService.kickPlayer(name, id);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable("id") int id) {
        findGame(id);
        gameService.deleteGameById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/play/{name}/ships")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Ship>> getShips(@PathVariable("name") String name) {
        List<Ship> ships = gameService.getShipsOfGame(name);
        return new ResponseEntity<List<Ship>>(ships, HttpStatus.OK);
    }

    @PutMapping("/play/{name}/expand/{hexPosition}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> useCardExpand(@PathVariable("name") String name,
            @PathVariable("hexPosition") int hexPosition) {
        try {
            Game game = gameService.findByName(name);
            Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        if (aux.getRol() == PlayerRol.SPECTATOR) {
            throw new AccessDeniedException("Estás viendo la partida en modo espectador, no puedes jugar.");
        } else {
                gameService.useExpandCard(game, hexPosition, aux);
        }
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotOwnedHex e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (YouCannotPlay e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PutMapping("/play/{name}/explore/{hexPositionOrigin}/{hexPositionTarget}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> useCardExplore(@PathVariable("name") String name,
            @PathVariable("hexPositionOrigin") int hexPositionOrigin,
            @PathVariable("hexPositionTarget") int hexPositionTarget) {
        try {
            Game game = gameService.findByName(name);
            Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        if (aux.getRol() == PlayerRol.SPECTATOR) {
            throw new AccessDeniedException("Estás viendo la partida en modo espectador, no puedes jugar.");
        } else {
                gameService.useExploreCard(game, hexPositionOrigin, hexPositionTarget, aux);
        }
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotOwnedHex e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (YouCannotPlay e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/play/{name}/exterminate/{hexPositionOrigin}/{hexPositionTarget}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> useCardExterminate(@PathVariable("name") String name,
            @PathVariable("hexPositionOrigin") int hexPositionOrigin,
            @PathVariable("hexPositionTarget") int hexPositionTarget) {
        try {
            Game game = gameService.findByName(name);
            Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        if (aux.getRol() == PlayerRol.SPECTATOR) {
            throw new AccessDeniedException("Estás viendo la partida en modo espectador, no puedes jugar.");
        } else {
                gameService.useExterminateCard(game, hexPositionOrigin, hexPositionTarget, aux);
        }
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotOwnedHex e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (YouCannotPlay e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
