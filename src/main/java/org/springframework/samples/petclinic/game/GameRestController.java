package org.springframework.samples.petclinic.game;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRol;
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
    private final HexService hexService;
    private final CardService cardService;

    @Autowired
    public GameRestController(GameService gameService, UserService userService, HexService hexService, CardService cardService) {
        this.gameService = gameService;
        this.userService = userService;
        this.hexService = hexService;
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> findAll() {
        return new ResponseEntity<>((List<Game>) gameService.getGames(), HttpStatus.OK);
    }

    @GetMapping("/publicas")
    public ResponseEntity<List<Game>> publicGames() {
        return new ResponseEntity<>((List<Game>) gameService.getPublicas(), HttpStatus.OK);
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
        gamePlayers.add(gameToGet.getHost());
        return new ResponseEntity<List<Player>>(gamePlayers, HttpStatus.OK);
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
    public ResponseEntity<Game> startGame(@PathVariable("name") String name) {
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        Game game = gameService.findByName(name);
        if (aux != game.getHost()){
            throw new AccessDeniedException("No puedes empezar la partida si no eres el host de la partida");
        }else{    
            gameService.startGame(name);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
        if (aux.getRol() != PlayerRol.HOST){
            throw new AccessDeniedException("No puedes echar a un jugador si no eres el host de la partida");
        }else{
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

    @PutMapping("/play/{name}/{hexId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> setUpShipInGameBoard(@PathVariable("name") String name, @PathVariable("hexId") int hexId) {
        Hex hex = hexService.findHexById(hexId);
        gameService.setUpShips(name, hex);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/play/{name}/expand/{hexPosition}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> useCardExpand(@PathVariable("name") String name, @PathVariable("hexPosition") int hexPosition) {
        cardService.useExpandCard(name, hexPosition);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/play/{name}/explore/{hexPositionOrigin}/{hexPositionTarget}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> useCardExplore(@PathVariable("name") String name, @PathVariable("hexPositionOrigin") int hexPositionOrigin, @PathVariable("hexPositionTarget") int hexPositionTarget) {
        cardService.useExploreCard(name, hexPositionOrigin, hexPositionTarget);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
