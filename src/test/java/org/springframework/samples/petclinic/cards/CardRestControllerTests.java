package org.springframework.samples.petclinic.cards;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardRestController;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.game.GameState;
import org.springframework.samples.petclinic.gameboard.GameBoardRestController;
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
        CardRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
public class CardRestControllerTests {

    private static final String BASE_URL = "/api/v1/cards";

    @SuppressWarnings("unused")
    @Autowired
    private CardRestController cardRestController;

    @MockBean
    private UserService userService;

    @MockBean
    private CardService cardService;

    @MockBean
    private GameService gameService;

    @Autowired
    MockMvc mockMvc;

    private User user1;
    private Player player1;
    private Card expand;
    private Card explore;
    private Card exterminate;
    private List<Card> playerCards;
    private Game partida1;

    @BeforeEach
    private void setUp() {

        Authorities playerAuth = new Authorities();
        playerAuth.setId(1);
        playerAuth.setAuthority("PLAYER");

        user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");
        user1.setPassword("password");
        user1.setAuthority(playerAuth);
        userService.saveUser(user1);

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("name1");
        player1.setLastName("last1");
        player1.setUser(user1);

        playerCards = new ArrayList<>();

        expand = new Card();
        expand.setId(1);
        expand.setType(CardType.EXPAND);
        expand.setPlayer(player1);
        playerCards.add(expand);

        explore = new Card();
        explore.setId(2);
        explore.setType(CardType.EXPLORE);
        explore.setPlayer(player1);
        playerCards.add(explore);

        exterminate = new Card();
        exterminate.setId(3);
        exterminate.setType(CardType.EXTERMINATE);
        exterminate.setPlayer(player1);
        playerCards.add(exterminate);

        partida1 = new Game();
        partida1.setId(1);
        partida1.setName("partidaTest");
        partida1.setPublica(true);
        partida1.setState(GameState.LOBBY);
        partida1.setStartTime(LocalDateTime.now());
        partida1.setHost(player1);

    }

    @Test
    @WithMockUser("user1")
    void shouldGetPlayerCards() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player1);
        when(this.cardService.getPlayerCards(player1.getId())).thenReturn(playerCards);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$.[0].type").value("EXPAND"))
                .andExpect(jsonPath("$.[0].player.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.[1].type").value("EXPLORE"))
                .andExpect(jsonPath("$.[1].player.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.[2].type").value("EXTERMINATE"))
                .andExpect(jsonPath("$.[2].player.firstName").value(player1.getFirstName()));
    }

    @Test
    @WithMockUser("user1")
    void shouldChangePerformingOrder() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player1);
        when(this.gameService.findByName(partida1.getName())).thenReturn(partida1);
        doNothing().when(this.cardService).setOrder(expand.getType(), player1, 2, partida1);

        mockMvc.perform(put(BASE_URL + "/" + partida1.getName() + "/expand" + "/{order}", 2).with(csrf())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("user1")
    void shouldNotChangePerformingOrder() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player1);
        when(this.gameService.findByName(partida1.getName())).thenReturn(partida1);
        doNothing().when(this.cardService).setOrder(expand.getType(), player1, 2, partida1);

        mockMvc.perform(put(BASE_URL + "/" + partida1.getName() + "/cadena" + "/{order}", 2).with(csrf())).andExpect(status().is5xxServerError());
    }

}
