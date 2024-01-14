package org.springframework.samples.petclinic.game;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.card.ActionsService;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class GameRestControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    GameService gs;
    @Autowired
    UserService us;
    @Autowired
    HexService hs;
    @Autowired
    ActionsService as;

    static final String BASE_URL = "/api/v1/game";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @Transactional
    @WithMockUser(username = "player1", authorities = { "PLAYER" })
    public void feasibleGameCreationTest() throws JsonProcessingException, Exception {

        // Configurar datos de prueba
        Game newGame = creatValidGame();
        BindingResult bindingResult = mock(BindingResult.class);

        // Configurar el comportamiento esperado de las dependencias
        when(bindingResult.hasErrors()).thenReturn(false);
        when(gs.createGame(any(Game.class))).thenReturn(newGame);
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newGame)))
                .andExpect(status().isCreated());
        // Comprobamos que se ha grabado efectivamente el juego en la bd:
        verify(gs, times(1)).createGame(eq(newGame));
    }

    @Test
    @Transactional
    @WithAnonymousUser
    public void feasibleGameCreationWithoutPriviledgesTest() throws JsonProcessingException, Exception {
        Game g = creatValidGame(); // This game is invalid since it has no name:
        g.setName("Turtles in time!");
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(BASE_URL)
                .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(g)))
                .andExpect(status().isUnauthorized());
        // Comprobamos que no se ha grabado el juego en la bd:
        assertEquals(g, gs.findByName(g.getName()));
    }

    private void asserNotNull(List<Game> gamesByName) {
    }

    private Game creatValidGame() {

        Player host = createValidPlayer();

        Game game = new Game();
        game.setName("Crazy smash bros session");
        game.setHost(host);
        game.setPublica(true);
        game.setState(GameState.LOBBY);
        return game;
    }

    private Player createValidPlayer() {

        Authorities authorities = new Authorities();
        authorities.setAuthority("PLAYER");

        User user = new User();
        user.setAuthority(authorities);
        user.setName("userPrueba");
        user.setPassword("userPrueba");

        Player player = new Player();
        player.setUser(user);

        return player;

    }
}