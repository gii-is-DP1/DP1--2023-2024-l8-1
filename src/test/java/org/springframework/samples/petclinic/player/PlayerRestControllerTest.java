package org.springframework.samples.petclinic.player;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;

/**
 * Test class for the {@link PlayerRestController}
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = {
        PlayerRestController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
public class PlayerRestControllerTest {

    private static final String BASE_URL = "/api/v1/players";

    @SuppressWarnings("unused")
    @Autowired
    private PlayerRestController playerRestController;

    @MockBean
    private UserService userService;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private ShipService shipService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private Player player1;
    private User user2;
    private Player player2;
    private User user3;
    private Player player3;
    private User user4;
    private Player player4;
    private List<Player> players;
    private List<Player> player1Friends;
    private List<Ship> playerShips;

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

        player1 = new Player();
        player1.setId(1);
        player1.setFirstName("name1");
        player1.setLastName("last1");
        player1.setUser(user1);

        user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setPassword("password");
        user2.setAuthority(playerAuth);

        player2 = new Player();
        player2.setId(2);
        player2.setFirstName("name2");
        player2.setLastName("last1");
        player2.setUser(user2);

        user3 = new User();
        user3.setId(3);
        user3.setUsername("user3");
        user3.setPassword("password");
        user3.setAuthority(playerAuth);

        player3 = new Player();
        player3.setId(3);
        player3.setFirstName("name3");
        player3.setLastName("last1");
        player3.setUser(user3);

        user4 = new User();
        user4.setId(4);
        user4.setUsername("user4");
        user4.setPassword("password");
        user4.setAuthority(playerAuth);

        player4 = new Player();
        player4.setId(4);
        player4.setFirstName("name4");
        player4.setLastName("last1");
        player4.setUser(user4);

        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        player1Friends = new ArrayList<>();
        player1Friends.add(player2);
        player1Friends.add(player3);

        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.IN_SUPPLY);

        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);

        Ship ship3 = new Ship();
        ship3.setPlayer(player1);
        ship3.setState(ShipState.IN_SUPPLY);

        playerShips = new ArrayList<>();
        playerShips.add(ship1);
        playerShips.add(ship2);
        playerShips.add(ship3);
    }

    @Test
    @WithMockUser("user1")
    public void shouldFindAllPlayers() throws Exception {
        when(this.playerService.findAll()).thenReturn(players);
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4))
                .andExpect(jsonPath("$.[0].firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.[1].firstName").value(player2.getFirstName()))
                .andExpect(jsonPath("$.[2].firstName").value(player3.getFirstName()))
                .andExpect(jsonPath("$.[3].firstName").value(player4.getFirstName()));
    }

    @Test
    @WithMockUser("user1")
    public void shouldFindCurrentPlayerFriends() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player1);
        when(this.playerService.getFriends()).thenReturn(player1Friends);

        mockMvc.perform(get(BASE_URL + "/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].firstName").value(player2.getFirstName()))
                .andExpect(jsonPath("$.[1].firstName").value(player3.getFirstName()));
    }

    @Test
    @WithMockUser("user1")
    public void shouldCreatePlayer() throws Exception {

        Authorities playerAuth = new Authorities();
        playerAuth.setId(1);
        playerAuth.setAuthority("PLAYER");

        User user5 = new User();
        user5.setId(4);
        user5.setUsername("user4");
        user5.setPassword("password");
        user5.setAuthority(playerAuth);

        Player player5 = new Player();
        player5.setId(4);
        player5.setFirstName("name4");
        player5.setLastName("last1");
        player5.setUser(user4);

        when(this.userService.findCurrentUser()).thenReturn(user5);
        when(this.playerService.savePlayer(player5)).thenReturn(player5);
        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player5))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("user1")
    void shouldUpdatePlayer() throws Exception {
        player1.setFirstName("UPDATED");
        player1.setLastName("UPDATED");

        when(this.playerService.findPlayerById(player1.getId())).thenReturn(player1);
        when(this.playerService.updatePlayer(any(Player.class), any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{id}", player1.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1))).andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(player1.getLastName()));
    }

    @Test
    @WithMockUser("user1")
    void shouldReturnNotFoundUpdateOwner() throws Exception {
        player1.setFirstName("UPDATED");
        player1.setLastName("UPDATED");

        when(this.playerService.findPlayerById(player1.getId())).thenThrow(ResourceNotFoundException.class);
        when(this.playerService.updatePlayer(any(Player.class), any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{id}", player1.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player1))).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("user1")
    void shouldStartSpectating() throws Exception {

        player1.setRol(PlayerRol.SPECTATOR);
        when(this.userService.findUser(user1.getUsername())).thenReturn(user1);
        when(this.playerService.findPlayerByUser(user1.getId())).thenReturn(player1);
        doNothing().when(this.playerService).startSpectating(player1);

        mockMvc.perform(put(BASE_URL + "/startSpectating/{username}", user1.getUsername()).with(csrf()))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser("user1")
    void shouldGetRemainingShips() throws Exception {

        when(this.userService.findUser(user1.getUsername())).thenReturn(user1);
        when(this.playerService.findPlayerByUser(user1.getId())).thenReturn(player1);
        when(this.shipService.selectShipsFromSupply(player1.getId())).thenReturn(playerShips);

        mockMvc.perform(get(BASE_URL + "/{username}/remainingShips", user1.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));
    }

    @Test
    @WithMockUser("user1")
    void shouldAddFriend() throws Exception {

        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.userService.findPlayerByUser(user1.getId())).thenReturn(player1);
        doNothing().when(this.playerService).addFriend(player1, player4.getId());
        mockMvc.perform(put(BASE_URL + "/add/{id}", player4.getId()).with(csrf()))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    @WithMockUser("user1")
    void shouldDeletePlayer() throws Exception {
        when(this.playerService.findPlayerById(player2.getId())).thenReturn(player2);

        doNothing().when(this.playerService).deletePlayer(player2.getId());
        mockMvc.perform(delete(BASE_URL + "/{id}", player2.getId()).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("user1")
    void shouldDeletePlayerFriend() throws Exception {
        doNothing().when(this.playerService).deleteFriend(player3.getId());;
        mockMvc.perform(delete(BASE_URL + "/delete/{id}", player3.getId()).with(csrf()))
                .andExpect(status().isOk());
    }

}
