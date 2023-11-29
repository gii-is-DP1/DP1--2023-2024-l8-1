package org.springframework.samples.petclinic.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class GameServiceTests {

    GameService gameService;
    UserService userService;
    PlayerService playerService;

    private User player2User;

    @Autowired
    public GameServiceTests(GameService gameService, UserService userService,
            PlayerService playerService) {
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
    }

    @BeforeEach
    public void setup() {

        Authorities playerAuth = new Authorities();
        playerAuth.setId(1);
        playerAuth.setAuthority("PLAYER");

        player2User = new User();
        player2User.setId(2);
        player2User.setUsername("player2Test");
        player2User.setPassword("player2Test");
        player2User.setAuthority(playerAuth);

    }

    @Test
    void shouldFindGames() {

        List<Game> games = gameService.getGames();

        Game game = EntityUtils.getById(games, Game.class, 3);
        assertEquals("prueba3", game.getName());
        assertEquals(3, game.getHost().getId());
        assertTrue(game.getPublica());
        assertEquals(GameState.OVER, game.getState());
        LocalDateTime expectedStartTime = LocalDateTime.parse("2023-11-11T21:16");
        assertEquals(expectedStartTime, game.getStartTime());
    }

    @Test
    void shouldFindSingleGame() {
        Game game = gameService.getById(1);
        assertEquals("prueba", game.getName());
        assertEquals(1, game.getHost().getId());
    }

    @Test
    void shouldNotFindSingleGameWithBadID() {
        Game game = gameService.getById(300);
        assertNull(game, "Se esperaba que el juego fuera nulo para el ID incorrecto.");
    }

    @Test
    void shouldReturnPublicGames() {
        List<Game> publicGames = gameService.getPublicas();
        assertEquals(2, publicGames.size(), "El tamaño de la lista de juegos públicos debería ser 1.");
    }

    @Test
    void shouldFindSingleGameByName() {
        Game game = gameService.findByName("prueba");
        assertEquals("prueba", game.getName());
        assertEquals(1, game.getHost().getId());
    }

    @Test
    void shouldNotFindSingleGameWithWrongName() {
        Game game = gameService.findByName("juego");
        assertNull(game, "Se esperaba que el juego fuera nulo para el nombre incorrecto.");
    }

    @Test
    void shouldFindGamePlayers() {

        List<Player> players = gameService.findGamePlayers("prueba");
        assertEquals(2, players.size());

        Player player2 = EntityUtils.getById(players, Player.class, 2);
        assertEquals("Play", player2.getFirstName());
        assertEquals("Yer", player2.getLastName());
        assertFalse(player2.getStartPlayer());

    }

    @Test
    @WithMockUser(username = "player2", authorities = "PLAYER")
    void shouldCreateAGameSucessfully() {

        List<Game> games = gameService.getGames();
        Integer gamesSize = games.size();

        Game newGame = createValidGame();
        gameService.createGame(newGame);

        List<Game> updatedGames = gameService.getGames();
        assertEquals(gamesSize + 1, updatedGames.size());

    }

    private Game createValidGame() {

        Game newGame = new Game();

        newGame.setName("partidaTest");
        newGame.setPublica(true);
        newGame.setState(GameState.LOBBY);
        newGame.setStartTime(LocalDateTime.now());

        return newGame;

    }

    @Test
    @WithMockUser(username = "player2", authorities = "PLAYER")
    void shouldNotCreateAValidGame() {

        assertThrows(TransactionSystemException.class, () -> {
            Game newGame = createAGameWithoutName();
            gameService.createGame(newGame);
        });
    }

    private Game createAGameWithoutName() {

        Game newGame = new Game();

        newGame.setPublica(true);
        newGame.setState(GameState.LOBBY);
        newGame.setStartTime(LocalDateTime.now());

        return newGame;

    }

    @Test
    void shouldUpdateGame() {

        Game game = gameService.findByName("prueba");
        game.setState(GameState.OVER);
        gameService.updateGame(game, game.getId());

        Game updatedGame = gameService.findByName("prueba");
        assertEquals(GameState.OVER, updatedGame.getState());

    }

    @Test
    void shouldNotUpdateGame() {

        Game game = gameService.findByName("prueba3");
        game.setName(null);
        
        assertThrows(TransactionSystemException.class, () -> {
        gameService.updateGame(game, game.getId());});

    }

    @Test
    @WithMockUser(username = "player2", authorities = "PLAYER")
    void shouldAddPlayerToGame() {

        Game game = gameService.findByName("prueba2");
        List<Player> players = game.getPlayers();
        Integer playersSize = players.size();

        gameService.joinPlayer("prueba");

        Game updatedGame = gameService.findByName("prueba");
        assertEquals(playersSize + 1, updatedGame.getPlayers().size());

    }

    @Test
    void shouldNotAddPlayerToGameIfNotAuthenticated() {

        assertThrows(ResourceNotFoundException.class, () -> {
        gameService.joinPlayer("prueba2");});

    }

    @Test
    void shouldRemovePlayerFromGame() {

        Game game = gameService.findByName("prueba");
        List<Player> players = game.getPlayers();
        Integer playersSize = players.size();

        gameService.kickPlayer("prueba", players.get(0).getId());

        Game updatedGame = gameService.findByName("prueba");
        assertEquals(playersSize - 1, updatedGame.getPlayers().size());

    }

    @Test
    void shouldDeleteGame() {

        List<Game> games = gameService.getGames();
        Integer gamesSize = games.size();

        gameService.deleteGameById(games.get(0).getId());

        List<Game> updatedGames = gameService.getGames();
        assertEquals(gamesSize - 1, updatedGames.size());

    }


}
