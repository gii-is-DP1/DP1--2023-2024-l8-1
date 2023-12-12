package org.springframework.samples.petclinic.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the {@link GameRestController}
 */
@WebMvcTest(value = {
        GameRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
public class GameControllerTests {

    @MockBean
    // Crea un bean simulado de tipo GameService para simular su comportamiento en
    // las pruebas
    GameService gameService;

    @MockBean
    UserService userService;

    @Autowired
    // Inyecta el objeto MockMvc que se utiliza para realizar solicitudes HTTP
    // simuladas y realizar aserciones sobre las respuestas
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    static final Integer TEST_GAME_ID = 1;
    static final String BASE_URL = "/api/v1/game";
    static final String URL_PUBLIC_GAMES = "/api/v1/game/publicas";
    static final String URL_PLAYER_GAMES = "/api/v1/game/lobby";

    private Game game1;
    private User player1user;
    private User player2user;
    private User player3user;
    private Player player1;
    private Player player2;
    private Player player3;

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

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("Mario");
        player1.setLastName("Reyes");
        player1.setUser(player1user);

        player2 = new Player();
        player2.setId(2);
        player2.setFirstName("Maria");
        player2.setLastName("Gonzalez");
        player2.setUser(player2user);

        player3 = new Player();
        player3.setId(3);
        player3.setFirstName("Juan");
        player3.setLastName("Garcia");
        player3.setUser(player3user);

        game1 = new Game();
        game1.setId(TEST_GAME_ID);
        game1.setName("partidaTest");
        game1.setPublica(true);
        game1.setState(GameState.LOBBY);
        game1.setStartTime(LocalDateTime.now());
        game1.setHost(player1);

        List<Player> playerGames = new ArrayList<>();
        playerGames.add(player1);
        playerGames.add(player2);
        playerGames.add(player3);

        game1.setPlayers(playerGames);

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
        game2.setStartTime(LocalDateTime.now());

        game3.setId(3);
        game3.setName("partidaTest_3");
        game3.setPublica(true);
        game3.setState(GameState.LOBBY);
        game3.setStartTime(LocalDateTime.now());

        when(gameService.getGames()).thenReturn(List.of(game1, game2, game3));

        mvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[?(@.id == 1)].name").value("partidaTest"))
                .andExpect(jsonPath("$[?(@.id == 2)].name").value("partidaTest_2"))
                .andExpect(jsonPath("$[?(@.id == 3)].name").value("partidaTest_3"));

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
        game2.setStartTime(LocalDateTime.now());

        game3.setId(3);
        game3.setName("partidaTest_3");
        game3.setPublica(true);
        game3.setState(GameState.LOBBY);
        game3.setStartTime(LocalDateTime.now());

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
    // Terminar este test
	@WithMockUser(username = "player1", authorities = "PLAYER")
    void shouldFindGamePlayers() throws Exception {

        when(gameService.findGamePlayers(game1.getName())).thenReturn(game1.getPlayers());
        mvc.perform(get(URL_PLAYER_GAMES + "/{name}", game1.getName())).andExpect(status().isOk());
        // 
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void shouldCreateGame() throws JsonProcessingException, Exception {

        Game g = creatValidGame();
        reset(gameService);
        when(gameService.saveGame(any(Game.class))).thenReturn(g);

        mvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(g)))
                .andExpect(status().isCreated());
        // Comprobamos que se ha intentado grabar el juego en la bd:
        verify(gameService, times(1)).saveGame(any(Game.class));
    }

    // Crear un objeto Game valido
    private Game creatValidGame() {
        Game g = new Game();
        g.setName("Crazy smash bros session");
        g.setPublica(false);
        g.setState(GameState.LOBBY);
        g.setStartTime(LocalDateTime.now());
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
                .andExpect(status().isBadRequest());
        // Comprobamos que no se ha grabado el juego en la BD:
        verify(gameService, never()).saveGame(any(Game.class));
    }

}
