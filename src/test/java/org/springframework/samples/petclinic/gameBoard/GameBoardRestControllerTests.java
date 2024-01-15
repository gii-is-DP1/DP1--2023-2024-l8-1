package org.springframework.samples.petclinic.gameBoard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.game.GameState;
import org.springframework.samples.petclinic.gameboard.GameBoard;
import org.springframework.samples.petclinic.gameboard.GameBoardRestController;
import org.springframework.samples.petclinic.gameboard.GameBoardService;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link GameBoardRestController}
 */
@WebMvcTest(value = {
        GameBoardRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
class GameBoardRestControllerTests {

    private static final int TEST_GAME_ID = 1;
    private static final String BASE_URL = "/api/v1/gameBoard";

    @SuppressWarnings("unused")
    @Autowired
    private GameBoardRestController gameBoardRestController;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameBoardService gameBoardService;

    @MockBean
    private UserService userService;

    @MockBean
    private HexService hexService;

    @MockBean
    private PlayerService playerService;

    @Autowired
    MockMvc mockMvc;

    private Game partida1;
    private User user1;
    private User user2;
    private User user3;
    private Player player1;
    private Player player2;
    private Player player3;
    private GameBoard gameBoard;
    private List<Hex> gbHexs;

    @BeforeEach
    void setUp() {
        Authorities playerAuth = new Authorities();
        playerAuth.setId(1);
        playerAuth.setAuthority("PLAYER");

        user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");
        user1.setPassword("password");
        user1.setAuthority(playerAuth);
        userService.saveUser(user1);

        user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setPassword("password");
        user2.setAuthority(playerAuth);

        user3 = new User();
        user3.setId(3);
        user3.setUsername("user3");
        user3.setPassword("password");
        user3.setAuthority(playerAuth);

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("name1");
        player1.setLastName("last1");
        player1.setUser(user1);
        playerService.savePlayer(player1);

        player2 = new Player();
        player2.setId(2);
        player2.setFirstName("name2");
        player2.setLastName("last2");
        player2.setUser(user2);

        player3 = new Player();
        player3.setId(3);
        player3.setFirstName("name3");
        player3.setLastName("last3");
        player3.setUser(user3);

        partida1 = new Game();
        partida1.setId(TEST_GAME_ID);
        partida1.setName("partidaTest");
        partida1.setPublica(true);
        partida1.setState(GameState.LOBBY);
        partida1.setStartTime(LocalDateTime.now());
        partida1.setHost(player1);


        List<Player> playerGames = new ArrayList<>();
        playerGames.add(player1);
        playerGames.add(player2);
        playerGames.add(player3);

        partida1.setPlayers(playerGames);

        gameBoard = new GameBoard();

        gbHexs = new ArrayList<>();
        for (int i = 0; i < 43; i++) {
            Hex hex = new Hex();
            hex.setPuntos(0);
            hex.setOccuped(false);
            hexService.save(hex);
            gbHexs.add(hex);
        }

    }

    @Test
    @WithMockUser("user1")
    void shouldGenerateGameBoard() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player1);
        when(this.gameBoardService.getGameBoardHexs(gameBoard)).thenReturn(gbHexs);
        when(this.gameService.findByName(partida1.getName())).thenReturn(partida1);
        when(this.gameBoardService.genRandomGameBoard(partida1.getName())).thenReturn(gameBoard);
        when(this.gameBoardService.getGameBoardByGame(partida1.getName())).thenReturn(gameBoard);

        mockMvc.perform(get(BASE_URL + "/{name}",
                partida1.getName())).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(43));
    }

    @Test
    @WithMockUser("user2")
    void shouldThrowException() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user2);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player2);
        when(this.gameService.findByName(partida1.getName())).thenReturn(partida1);

        mockMvc.perform(get(BASE_URL + "/{name}",
                partida1.getName())).andExpect(status().is4xxClientError());
    }



}
