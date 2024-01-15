package org.springframework.samples.petclinic.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
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
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class GameServiceTests {

    GameService gameService;
    UserService userService;
    PlayerService playerService;
    RoundService roundService;
    PhaseService phaseService;
    TurnService turnService;
    GameBoardService gameBoardService;
    ShipService shipService;
    HexService hexService;

    private User player2User;

    @Autowired
    public GameServiceTests(GameService gameService, UserService userService,
            PlayerService playerService, RoundService roundService, PhaseService phaseService, TurnService turnService,
            GameBoardService gameBoardService, ShipService shipService, HexService hexService) {
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
        this.roundService = roundService;
        this.phaseService = phaseService;
        this.turnService = turnService;
        this.gameBoardService = gameBoardService;
        this.shipService = shipService;
        this.hexService = hexService;
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
        assertEquals(3, players.size());

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
        List<Player> ls = new ArrayList<>();
        Player player = playerService.findPlayerById(1);
        newGame.setHost(player);
        newGame.setName("partidaTest");
        newGame.setPublica(true);
        newGame.setState(GameState.LOBBY);
        newGame.setStartTime(LocalDateTime.of(2023, 11, 11, 11, 11, 11));
        ls.add(playerService.findPlayerById(2));
        ls.add(playerService.findPlayerById(3));
        newGame.setPlayers(ls);
        return newGame;

    }

    @Test
    void shouldNotCreateAValidGame() {

        assertThrows(ResourceNotFoundException.class, () -> {
            Game newGame = new Game();
            gameService.createGame(newGame);
        });
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

        assertThrows(ConstraintViolationException.class, () -> {
            gameService.updateGame(game, game.getId());
        });

    }

    @Test
    @WithMockUser(username = "player2", authorities = "PLAYER")
    void shouldAddPlayerToGame() {

        Game game = gameService.findByName("prueba2");
        List<Player> players = game.getPlayers();
        Integer playersSize = players.size();

        gameService.joinPlayer("prueba2");

        Game updatedGame = gameService.findByName("prueba2");
        assertEquals(playersSize + 1, updatedGame.getPlayers().size());

    }

    @Test
    void shouldNotAddPlayerToGameIfNotAuthenticated() {

        assertThrows(ResourceNotFoundException.class, () -> {
            gameService.joinPlayer("prueba2");
        });

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
    
    @Test
    // Debería crear una ronda inicial compuesta de tres fases si se pasa un Game si
    // el parámetro isInitial es verdadero
    void shouldAddInitialRound() {

        Game game = gameService.saveGame(createValidGame());

        Round round = gameService.addRound(game, true);
        Integer numPhases = round.getPhases().size();
        Round round2 = game.getRounds().get(0);

        assertEquals(3, numPhases);
        assertEquals(round, round2);

    }

    @Test
    // Si se crea una ronda ordinaria debería generar tres fases de juego, una de
    // puntuación y otra de ordenar cartas (5 en total)
    void shouldAddOrdinaryRound() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        List<Player> playerList = gameService.findGamePlayers(game.getName());

        Round round = gameService.addRound(game, false);
        Integer numPhases = round.getPhases().size();

        int p1 = playerList.indexOf((game.getRounds().get(0).getPhases().get(0)).getTurns().get(0).getPlayer());
        int playerInicial = p1 + 1 > 2
                ? p1 + 1 - 3
                : p1 + 1;
        int p2 = playerList.indexOf((round.getPhases().get(0)).getTurns().get(0).getPlayer());

        Boolean isPoint = round.getPhases().get(3).getIsPoint();
        Boolean isOrder = round.getPhases().get(4).getIsOrder();

        assertEquals(5, numPhases);
        assertEquals(playerInicial, p2);
        assertEquals(true, isPoint);
        assertEquals(true, isOrder);

    }

    @Test
    //
    void shouldStartGameSuccessfully() {

        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());

        // Debe haber una ronda inicial
        Round rondaInicial = game.getRounds().get(0);
        assertNotNull(rondaInicial);
        // El score de cada jugador debe estar a 0
        List<Player> players = gameService.findGamePlayers(game.getName());
        assertTrue(players.stream().allMatch(p -> p.getScore().equals(0)));
        // Cada jugador debe tener 15 naves
        Boolean nNaves = gameService.findGamePlayers(game.getName()).stream().allMatch(p -> p.getShips().size() == 15);
        assertTrue(nNaves);
        // Cada jugador debe tener 3 cartas
        Boolean nCartas = gameService.findGamePlayers(game.getName()).stream().allMatch(p -> p.getCards().size() == 3);
        assertTrue(nCartas);

    }

    @Test
    // El jugador debería poder pasar turno en su turno
    void shouldSkipTurn() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).setIsOver(true);
        roundService.saveRound(game.getRounds().get(0));
        gameService.addRound(game, false);

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Boolean beforeSkip = turn.getIsOver();
        Player player = turn.getPlayer();

        gameService.skipTurn(game, player);

        Boolean afterSkip = turn.getIsOver();

        assertEquals(!beforeSkip, afterSkip);
    }

    @Test
    // El jugador no debería poder pasar turno en el turno de otro jugador
    void shouldNotSkipTurn() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).setIsOver(true);
        roundService.saveRound(game.getRounds().get(0));
        gameService.addRound(game, false);

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = turn.getPlayer();

        Player player2 = playerService.findPlayerById(4);

        assertNotEquals(player, player2);

        assertThrows(AccessDeniedException.class, () -> gameService.skipTurn(game, player2), "No es tu turno.");

    }

    @Test
    // Un jugador no debería poder pasar turno en la ronda inicial
    void shouldNotSkipTurnInInitialRound() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = turn.getPlayer();

        assertThrows(AccessDeniedException.class, () -> gameService.skipTurn(game, player),
                "No se puede pasar en la ronda inicial.");
    }


    @Test
    // No se pueden ordenar las cartas cuando no toca ordenarlas
    void shoulsNotOrderCardsIfIsNotTime() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).getPhases().get(0).setIsOver(true);
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(0));

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        assertThrows(AccessDeniedException.class, () -> gameService.orderCards(game, player),
                "No es hora de ordenar las cartas.");
    }

    @Test
    // El jugador debería poder ordenar las cartas en su turno
    void shouldAddRoundAfterInitialOrder() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).getPhases().get(0).setIsOver(true);
        game.getRounds().get(0).getPhases().get(1).setIsOver(true);
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(0));
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(1));

        game.getRounds().get(0).getPhases().get(2).getTurns().get(0).setIsOver(true);
        game.getRounds().get(0).getPhases().get(2).getTurns().get(1).setIsOver(true);

        turnService.saveTurn(game.getRounds().get(0).getPhases().get(2).getTurns().get(0));
        turnService.saveTurn(game.getRounds().get(0).getPhases().get(2).getTurns().get(1));

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        gameService.orderCards(game, player);

        assertEquals(2, game.getRounds().size());
        assertEquals(GameState.IN_PROGRESS, game.getState());

    }

    @Test
    void shouldAddRoundAfterFinishingOrder() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).setIsOver(true);
        roundService.saveRound(game.getRounds().get(0));
        Round round = gameService.addRound(game, false);
        for (int i = 0; i < 4; i++) {
            round.getPhases().get(i).setIsOver(true);
            phaseService.savePhase(round.getPhases().get(i));
        }

        round.getPhases().get(4).getTurns().get(0).setIsOver(true);
        round.getPhases().get(4).getTurns().get(1).setIsOver(true);

        turnService.saveTurn(game.getRounds().get(0).getPhases().get(2).getTurns().get(0));
        turnService.saveTurn(game.getRounds().get(0).getPhases().get(2).getTurns().get(1));

        Round round2 = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round2.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        gameService.orderCards(game, player);

        assertEquals(3, game.getRounds().size());
    }

    private Integer createGameBoard(Game game, Player player, Integer puntos) {
        GameBoard gb = gameBoardService.genRandomGameBoard(game.getName());
        List<Hex> hexs = gameService.getGameBoardHexs(gb);
        Hex target = null;
        Integer position = 0;
        for (int i = 0; i < hexs.size(); i++) {
            if (hexs.get(i).getPuntos() == puntos) {
                target = hexs.get(i);
                position = i;
                break;
            }
        }
        List<Ship> ls = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Ship ship = player.getShips().get(i);
            ship.setHex(target);
            ship.setState(ShipState.ON_GAME);
            shipService.save(ship);
            ls.add(ship);
        }
        target.setShips(ls);
        target.setOccuped(true);
        hexService.save(target);
        return position;

    }

    @Test
    // El juagador debería poder usar la carta expandir en su turno
    void shouldUseCardInPlayersTurn() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).setIsOver(true);
        roundService.saveRound(game.getRounds().get(0));
        gameService.addRound(game, false);
        List<Player> players = gameService.findGamePlayers(game.getName());

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        Integer position = createGameBoard(game, player, 1);

        Integer usesBefore = player.getCards().get(round.getPhases().indexOf(phase)).getUsesLeft();

        gameService.useExpandCard(game, position, player);

        Integer usesAfter = player.getCards().get(round.getPhases().indexOf(phase)).getUsesLeft();

        assertEquals(usesBefore-1, usesAfter);
    }

    @Test
    // El jugador no debería poder usar la carta si no es su turno
    void shouldNotUseCardInOtherPlayerTurn() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());

        List<Player> players = gameService.findGamePlayers(game.getName());

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        Player player2 = playerService.findPlayerById(4);

        assertNotEquals(player, player2);

        assertThrows(AccessDeniedException.class, () -> gameService.useExpandCard(game, 3, player2), "No es tu turno.");

    }

    @Test
    // No puede usarse la carta Expandir en la fase de ordenar cartas
    void shouldNotUseCardInOrderCardsPhase() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        game.getRounds().get(0).getPhases().get(0).setIsOver(true);
        game.getRounds().get(0).getPhases().get(1).setIsOver(true);
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(0));
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(1));

        game.getRounds().get(0).getPhases().get(2).getTurns().get(0).setIsOver(true);
        game.getRounds().get(0).getPhases().get(2).getTurns().get(1).setIsOver(true);

        turnService.saveTurn(game.getRounds().get(0).getPhases().get(2).getTurns().get(0));
        turnService.saveTurn(game.getRounds().get(0).getPhases().get(2).getTurns().get(1));

        List<Player> players = gameService.findGamePlayers(game.getName());

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player player = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        assertThrows(AccessDeniedException.class, () -> gameService.useExpandCard(game, 3, player),
                "Es hora de ordenar las cartas.");

    }

    @Test
    // Las naves que no puedan ser abastecidas por un hexagono al final de la partida vuelven a la reserva de cada jugador
    void shouldReturnShipsToSupply() {
        Game game = gameService.saveGame(createValidGame());
        gameService.startGame(game.getName());
        List<Player> players = gameService.findGamePlayers(game.getName());
        Integer position = createGameBoard(game, players.get(0), 0);

        Hex hexBefore = gameService.getGameBoardHexs(game.getGameBoard()).get(position);
        long nNavesBefore = players.get(0).getShips().stream().filter(s -> s.getState() == ShipState.IN_SUPPLY).count();

        assertTrue(hexBefore.getShips().size() > hexBefore.getPuntos()+1);

        gameService.limpiarExtras(game);

        Hex hexAfter = gameService.getGameBoardHexs(game.getGameBoard()).get(position);
        long nNavesAfter = players.get(0).getShips().stream().filter(s -> s.getState() == ShipState.IN_SUPPLY).count();

        assertEquals(hexAfter.getShips().size(), hexBefore.getPuntos()+1);
        assertEquals(nNavesBefore +1, nNavesAfter);
        
    }

}
