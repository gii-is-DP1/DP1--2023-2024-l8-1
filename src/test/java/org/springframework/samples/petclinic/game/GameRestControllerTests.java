package org.springframework.samples.petclinic.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRol;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.ipc.http.HttpSender.Response;

/**
 * Test class for the {@link GameRestController}
 */
@WebMvcTest(value = {
        GameRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
public class GameRestControllerTests {

    @SuppressWarnings("unused")
    @Autowired
    private GameRestController gameRestController;

    @MockBean
    // Crea un bean simulado de tipo GameService para simular su comportamiento en
    // las pruebas
    private GameService gameService;

    @MockBean
    private UserService userService;

    @MockBean
    private HexService hexService;

    @Autowired
    // Inyecta el objeto MockMvc que se utiliza para realizar solicitudes HTTP
    // simuladas y realizar aserciones sobre las respuestas
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    static final Integer TEST_GAME_ID = 1;
    static final String BASE_URL = "/api/v1/game";
    static final String URL_PUBLIC_GAMES = "/api/v1/game/publicas";
    static final String URL_PLAYER_GAMES = "/api/v1/game/lobby";

    private Game game1;
    private User player1user;
    private User player2user;
    private User player3user;
    private User playerSpectatoruser;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player playerSpectator;
    private List<Player> allGamePlayers;
    private List<Player> gamePlayers;
    private List<Ship> shipsInGame;

    @BeforeEach
    private void setUp() {

        Authorities authorities = new Authorities();
        authorities.setAuthority("PLAYER");

        player1user = new User();
        player1user.setId(1);
        player1user.setName("player1user");
        player1user.setAuthority(authorities);

        player2user = new User();
        player2user.setId(2);
        player2user.setName("player2user");
        player2user.setAuthority(authorities);

        player3user = new User();
        player3user.setId(3);
        player3user.setName("player3user");
        player3user.setAuthority(authorities);

        playerSpectatoruser = new User();
        playerSpectatoruser.setId(4);
        playerSpectatoruser.setName("playerSpectatoruser");
        playerSpectatoruser.setAuthority(authorities);

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("Mario");
        player1.setLastName("Reyes");
        player1.setScore(2);
        player1.setUser(player1user);
        player1.setRol(PlayerRol.HOST);

        player2 = new Player();
        player2.setId(2);
        player2.setFirstName("Maria");
        player2.setLastName("Gonzalez");
        player2.setScore(13);
        player2.setUser(player2user);
        player2.setRol(PlayerRol.GUEST);

        player3 = new Player();
        player3.setId(3);
        player3.setFirstName("Juan");
        player3.setLastName("Garcia");
        player3.setScore(5);
        player3.setUser(player3user);

        playerSpectator = new Player();
        playerSpectator.setId(4);
        playerSpectator.setFirstName("Juan");
        playerSpectator.setLastName("Garcia");
        playerSpectator.setUser(playerSpectatoruser);
        playerSpectator.setRol(PlayerRol.SPECTATOR);

        game1 = new Game();
        game1.setId(TEST_GAME_ID);
        game1.setName("partidaTest");
        game1.setPublica(true);
        game1.setState(GameState.LOBBY);
        game1.setStartTime(LocalDateTime.now());
        game1.setHost(player1);

        allGamePlayers = new ArrayList<>();
        allGamePlayers.add(player1);
        allGamePlayers.add(player2);
        allGamePlayers.add(player3);

        gamePlayers = new ArrayList<>();
        gamePlayers.add(player2);
        gamePlayers.add(player3);

        game1.setPlayers(gamePlayers);

        shipsInGame = new ArrayList<>();

        Hex hex = new Hex();
        hex.setId(1);
        hex.setOccuped(true);
        hex.setPosition(2);
        hex.setPuntos(1);

        Ship ship1 = new Ship();
        ship1.setId(1);
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        ship1.setHex(hex);

        Ship ship2 = new Ship();
        ship2.setId(2);
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);

        Ship ship3 = new Ship();
        ship3.setId(2);
        ship3.setPlayer(player1);
        ship3.setState(ShipState.REMOVED);

        shipsInGame.add(ship1);
        shipsInGame.add(ship2);
        shipsInGame.add(ship3);

    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void shouldFindAll() throws Exception {

        Game game2 = new Game();
        Game game3 = new Game();

        game2.setId(2);
        game2.setName("partidaTest_2");
        game2.setPublica(true);
        game2.setState(GameState.LOBBY);
        // game2.setStartTime(LocalDateTime.now());

        game3.setId(3);
        game3.setName("partidaTest_3");
        game3.setPublica(true);
        game3.setState(GameState.LOBBY);
        // game3.setStartTime(LocalDateTime.now());

        when(gameService.getGames()).thenReturn(List.of(game1, game2, game3));

        mvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$.[0].name").value("partidaTest"))
                .andExpect(jsonPath("$.[1].name").value("partidaTest_2"))
                .andExpect(jsonPath("$.[2].name").value("partidaTest_3"));

    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void shouldFindPublicGames() throws Exception {

        Game game2 = new Game();
        Game game3 = new Game();

        game2.setId(2);
        game2.setName("partidaTest_2");
        game2.setPublica(true);
        game2.setState(GameState.LOBBY);
        // game2.setStartTime(LocalDateTime.now());

        game3.setId(3);
        game3.setName("partidaTest_3");
        game3.setPublica(true);
        game3.setState(GameState.LOBBY);
        // game3.setStartTime(LocalDateTime.now());

        when(gameService.getPublicas()).thenReturn(List.of(game1, game2, game3));

        mvc.perform(get(URL_PUBLIC_GAMES)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[?(@.id == 1)].name").value("partidaTest"))
                .andExpect(jsonPath("$[?(@.id == 2)].name").value("partidaTest_2"))
                .andExpect(jsonPath("$[?(@.id == 3)].name").value("partidaTest_3"));

    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    void shouldReturnGame() throws Exception {
        when(gameService.getById(TEST_GAME_ID)).thenReturn(game1);
        mvc.perform(get(BASE_URL + "/{id}", TEST_GAME_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_GAME_ID))
                .andExpect(jsonPath("$.name").value(game1.getName()))
                .andExpect(jsonPath("$.publica").value(game1.getPublica()))
                .andExpect(jsonPath("$.state").value(game1.getState().toString()));
    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    void shouldReturnNotFoundGame() throws Exception {
        when(gameService.getById(TEST_GAME_ID)).thenThrow(ResourceNotFoundException.class);
        mvc.perform(get(BASE_URL + "/{id}", TEST_GAME_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    void shouldFindGamePlayers() throws Exception {

        when(gameService.findByName(game1.getName())).thenReturn(game1);
        when(gameService.findGamePlayers(game1.getName())).thenReturn(game1.getPlayers());
        mvc.perform(get(URL_PLAYER_GAMES + "/{name}", game1.getName())).andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    void shouldNotFindGamePlayers() throws Exception {

        when(gameService.findByName(game1.getName())).thenReturn(null);
        mvc.perform(get(URL_PLAYER_GAMES + "/{name}", game1.getName())).andExpect(status().is4xxClientError());

    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void shouldCreateGame() throws JsonProcessingException, Exception {

        Game g = creatValidGame();
        when(gameService.saveGame(any(Game.class))).thenReturn(g);

        mvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(g)))
                .andExpect(status().isCreated());
        
    }

    // Crear un objeto Game valido
    private Game creatValidGame() {
        Game g = new Game();
        g.setName("Crazy");
        g.setPublica(false);
        g.setState(GameState.LOBBY);
        g.setHost(player1);
        return g;
    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void unfeasibleGameCreationTest() throws JsonProcessingException, Exception {
        Game g = new Game(); // This game is invalid since it has no name:

        mvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(g))) // Transformamos el objeto en JSON
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void shouldUpdateGame() throws JsonProcessingException, Exception {

        game1.setName("UpdatedName");
        when(this.gameService.getById(game1.getId())).thenReturn(game1);
        when(gameService.updateGame(game1, game1.getId())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/" + game1.getId().toString())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game1)))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @WithMockUser("user1")
    public void shouldReturnSortedList() throws Exception {
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.findGamePlayers(game1.getName())).thenReturn(allGamePlayers);

        mvc.perform(get(BASE_URL + "/getWinner/{name}", game1.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$.[0].score").value("13"))
                .andExpect(jsonPath("$.[0].firstName").value(player2.getFirstName()))
                .andExpect(jsonPath("$.[1].score").value("5"))
                .andExpect(jsonPath("$.[1].firstName").value(player3.getFirstName()))
                .andExpect(jsonPath("$.[2].score").value("2"))
                .andExpect(jsonPath("$.[2].firstName").value(player1.getFirstName()));
    }

    @Test
    @WithMockUser("user1")
    public void shouldReturnException() throws Exception {
        when(this.gameService.findByName("noValido")).thenReturn(null);

        mvc.perform(get(BASE_URL + "/getWinner/{name}", "noValido")).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("user1")
    public void shouldStartGame() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.startGame(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/start/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    public void shouldReturnAccessDeniedException() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(player2user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player2);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.startGame(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/start/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("user1")
    public void shouldReturnBadRequestException() throws Exception {
        gamePlayers.remove(1);
        game1.setPlayers(gamePlayers);
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.startGame(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/start/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is5xxServerError());
    }

    private Game addRoundToGame(Game game) {
        Round round = new Round();
        round.setIsOver(false);
        Phase phase = new Phase();
        phase.setIsOver(false);
        phase.setIsOrder(false);
        phase.setIsPoint(false);
        round.setPhases(List.of(phase));
        game.setRounds(List.of(round));
        return game;
    }

    private Game addPointPhaseToGame(Game game) {
        Round round = new Round();
        round.setIsOver(true);
        Round round2 = new Round();
        round2.setIsOver(false);
        List<Phase> ls = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Phase phase = new Phase();
            phase.setIsOver(true);
            ls.add(phase);
        }
        Phase phase1 = new Phase();
        phase1.setIsOver(false);
        phase1.setIsPoint(true);
        ls.add(phase1);
        round2.setPhases(ls);
        game.setRounds(List.of(round, round2));
        return game;
    }

    private Game addGamePhaseToGame(Game game) {
        Round round = new Round();
        round.setIsOver(true);
        Round round2 = new Round();
        round2.setIsOver(false);
        Phase phase = new Phase();
        phase.setIsOrder(false);
        phase.setIsPoint(false);
        round2.setPhases(List.of(phase));
        game.setRounds(List.of(round, round2));
        return game;
    }

    @Test
    @WithMockUser("user1")
    public void shouldCallInitialRound() throws Exception {
        Game game = addRoundToGame(game1);
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game.getName())).thenReturn(game);
        when(this.gameService.initialRound(game.getName(), 3, 20, player1)).thenReturn(game);

        mvc.perform(put(BASE_URL + "/setHex/{name}/{sector}/{hexPosition}", game.getName(), 3, 20).with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    public void shouldCallPointPhase() throws Exception {
        Game game = addPointPhaseToGame(game1);
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game.getName())).thenReturn(game);
        when(this.gameService.pointPhase(game, 3, player1)).thenReturn(game);

        mvc.perform(put(BASE_URL + "/setHex/{name}/{sector}/{hexPosition}", game.getName(), 3, 20).with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    public void spectatorShouldntSetHex() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(playerSpectatoruser);
        when(this.userService.findPlayerByUser(playerSpectatoruser.getId())).thenReturn(playerSpectator);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/setHex/{name}/{sector}/{hexPosition}", game1.getName(), 3, 20).with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("user1")
    public void shouldSkipTurn() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/skipTurn/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    public void spectatorShouldntSkip() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(playerSpectatoruser);
        when(this.userService.findPlayerByUser(playerSpectatoruser.getId())).thenReturn(playerSpectator);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/skipTurn/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("user1")
    public void shouldReturnInitialTrue() throws Exception {
        Game game = addRoundToGame(game1);
        when(this.gameService.findByName(game.getName())).thenReturn(game);

        mvc.perform(get(BASE_URL + "/isInitial/{name}", game.getName())).andExpect(jsonPath("$").value(true));
    }

    @Test
    @WithMockUser("user1")
    public void shouldReturnInitialFalse() throws Exception {
        Game game = addPointPhaseToGame(game1);
        when(this.gameService.findByName(game.getName())).thenReturn(game);

        mvc.perform(get(BASE_URL + "/isInitial/{name}", game.getName())).andExpect(jsonPath("$").value(false));
    }

    /*
     * @Test
     * 
     * @WithMockUser("user1")
     * public void shouldGetCurrentTurn() throws Exception {
     * Turn turn = new Turn();
     * turn.setIsOver(false);
     * turn.setPlayer(player1);
     * when(this.gameService.findByName(game1.getName())).thenReturn(game1);
     * when(this.gameService.getCurrentTurn(game1)).thenReturn(turn);
     * 
     * String responseContent = mvc.perform(get(BASE_URL + "/getCurrentTurn/{name}",
     * game1.getName()))
     * .andExpect(status().isOk())
     * .andReturn().getResponse().getContentAsString();
     * 
     * System.out.println("Response Content: " + responseContent);
     * 
     * mvc.perform(get(BASE_URL + "/getCurrentTurn/{name}", game1.getName()))
     * .andExpect(jsonPath("$.[0].player.user.username").value(turn.getPlayer().
     * getUser().getUsername()));
     * }
     */

    @Test
    @WithMockUser("user1")
    public void shouldGetCurrentPhase() throws Exception {
        Game game = addPointPhaseToGame(game1);
        Phase phase = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get().getPhases().stream()
                .filter(s -> !s.getIsOver()).findFirst().get();
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.getCurrentPhase(game1)).thenReturn(phase);

        mvc.perform(get(BASE_URL + "/getCurrentPhase/{name}", game1.getName()))
                .andExpect(jsonPath("$.isPoint").value(true));
    }

    @Test
    @WithMockUser("user1")
    public void shouldSetOrder() throws Exception {
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);

        mvc.perform(put(BASE_URL + "/setOrder/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    public void spectatorShouldntSetOrder() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(playerSpectatoruser);
        when(this.userService.findPlayerByUser(playerSpectatoruser.getId())).thenReturn(playerSpectator);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);

        mvc.perform(put(BASE_URL + "/setOrder/{name}", game1.getName()).with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("user1")
    public void shouldGetCurrentAction() throws Exception {
        Game game = addGamePhaseToGame(game1);
        Phase phase = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get().getPhases().stream()
                .filter(s -> !s.getIsOver()).findFirst().get();
        Card card = new Card();
        card.setType(CardType.EXPAND);
        when(this.gameService.findByName(game.getName())).thenReturn(game);
        when(this.gameService.getCurrentPhase(game1)).thenReturn(phase);
        when(this.gameService.getCurrentAction(game)).thenReturn(card);

        mvc.perform(get(BASE_URL + "/getAction/{name}", game.getName())).andExpect(status().isOk()).andExpect(jsonPath("$.[0]").value("EXPAND"));
    }

    @Test
    @WithMockUser("user1")
    public void shouldGetNada() throws Exception {
        Game game = addRoundToGame(game1);
        Phase phase = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get().getPhases().stream()
                .filter(s -> !s.getIsOver()).findFirst().get();
        when(this.gameService.findByName(game.getName())).thenReturn(game);
        when(this.gameService.getCurrentPhase(game1)).thenReturn(phase);

        mvc.perform(get(BASE_URL + "/getAction/{name}", game.getName())).andExpect(status().isOk()).andExpect(jsonPath("$.[0]").value("nada"));
    }

    @Test
    @WithMockUser("user1")
    public void shouldkickPlayerFromGame() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.getById(game1.getId())).thenReturn(game1);
        when(this.gameService.kickPlayer(game1.getName(), player2.getId())).thenReturn(game1);
        
        mvc.perform(delete(BASE_URL + "/lobby/{name}/{id}", game1.getName(), player2.getId()).with(csrf()))
                .andExpect(status().is2xxSuccessful());
        
    }

    @Test
    @WithMockUser("user1")
    public void shouldNotkickPlayerFromGameIfPlayerIsNotHost() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(player2user);
        when(this.userService.findPlayerByUser(player2user.getId())).thenReturn(player2);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        when(this.gameService.getById(game1.getId())).thenReturn(game1);
        when(this.gameService.kickPlayer(game1.getName(), player3.getId())).thenReturn(game1);
        
        mvc.perform(delete(BASE_URL + "/lobby/{name}/{id}", game1.getName(), player3.getId()).with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser("user1")
    public void shouldDeleteGame() throws Exception {
        when(this.gameService.getById(game1.getId())).thenReturn(game1);

        doNothing().when(this.gameService).deleteGameById(game1.getId());
        mvc.perform(delete(BASE_URL + "/{id}", game1.getId()).with(csrf()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    public void shouldGetShips() throws Exception {

        when(this.gameService.getShipsOfGame(game1.getName())).thenReturn(shipsInGame);
        mvc.perform(get(BASE_URL + "/play/{name}/ships", game1.getName())).andExpect(jsonPath("$.size()").value(3));
    }

    @Test
    @WithMockUser("user1")
    public void shouldUseCard() throws Exception {

        Hex hexTest = new Hex();
        hexTest.setId(1);
        hexTest.setPosition(5);
        hexTest.setOccuped(false);
        hexTest.setPuntos(0);

        when(this.userService.findCurrentUser()).thenReturn(player1user);
        when(this.userService.findPlayerByUser(player1user.getId())).thenReturn(player1);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        doNothing().when(this.gameService).useExpandCard(game1, hexTest.getPosition(), player1);

        mvc.perform(put(BASE_URL + "/play/{name}/expand/{hexPosition}", game1.getName(), hexTest.getPosition())
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("user1")
    public void shouldNotAllowUseCardIfPlayerRoleIsSpectator() throws Exception {

        Hex hexTest = new Hex();
        hexTest.setId(1);
        hexTest.setPosition(5);
        hexTest.setOccuped(false);
        hexTest.setPuntos(0);

        when(this.userService.findCurrentUser()).thenReturn(playerSpectatoruser);
        when(this.userService.findPlayerByUser(playerSpectatoruser.getId())).thenReturn(playerSpectator);
        when(this.gameService.findByName(game1.getName())).thenReturn(game1);
        doNothing().when(this.gameService).useExpandCard(game1, hexTest.getPosition(), playerSpectator);

        mvc.perform(put(BASE_URL + "/play/{name}/expand/{hexPosition}", game1.getName(), hexTest.getPosition())
                .with(csrf()))
                .andExpect(status().is4xxClientError());

    }

}
