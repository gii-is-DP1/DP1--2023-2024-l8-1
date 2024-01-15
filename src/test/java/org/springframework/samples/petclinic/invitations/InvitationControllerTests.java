package org.springframework.samples.petclinic.invitations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.invitation.Invitation;
import org.springframework.samples.petclinic.invitation.InvitationRestController;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.invitation.InvitationType;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Test class for {@link InvitationRestController}
 *
 * 
 */
@WebMvcTest(controllers = InvitationRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class))
public class InvitationControllerTests {

    private static final String BASE_URL = "/api/v1/invitations";

    @SuppressWarnings("unused")
    @Autowired
    InvitationRestController invitationRestController;

    @MockBean
    InvitationService invitationService;

    @MockBean
    UserService userService;

    @MockBean
    PlayerService playerService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc; 

    private User user1;
    private User user2;
    private User user3;
    private Player source;
    private Player target1;
    private Player target2;
    private List<Invitation> invitaciones;
    private List<Invitation> invitationsForSource;
    private Invitation invitationToDelete;

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

        source = new Player();
        source.setId(1);
        source.setFirstName("name1");
        source.setLastName("last1");
        source.setUser(user1);

        user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setPassword("password");
        user2.setAuthority(playerAuth);

        target1 = new Player();
        target1.setId(2);
        target1.setFirstName("name2");
        target1.setLastName("last1");
        target1.setUser(user2);

        user3 = new User();
        user3.setId(3);
        user3.setUsername("user3");
        user3.setPassword("password");
        user3.setAuthority(playerAuth);

        target2 = new Player();
        target2.setId(3);
        target2.setFirstName("name3");
        target2.setLastName("last1");
        target2.setUser(user3);

        Invitation invitation1 = new Invitation();
        invitation1.setId(5);
        invitation1.setDiscriminator(InvitationType.FRIENDSHIP);
        invitation1.setPlayerSource(source);
        invitation1.setPlayerTarget(target1);

        Invitation invitation2 = new Invitation();
        invitation2.setId(6);
        invitation2.setDiscriminator(InvitationType.FRIENDSHIP);
        invitation2.setPlayerSource(source);
        invitation2.setPlayerTarget(target1);

        invitaciones = new ArrayList<>();
        invitaciones.add(invitation1);
        invitaciones.add(invitation2);

        Invitation invitationForSource = new Invitation();
        invitationForSource.setId(7);
        invitationForSource.setDiscriminator(InvitationType.FRIENDSHIP);
        invitationForSource.setPlayerSource(target1);
        invitationForSource.setPlayerTarget(source);

        invitationsForSource = new ArrayList<>();
        invitationsForSource.add(invitationForSource);

        invitationToDelete = new Invitation();
        invitationToDelete.setId(1);
        invitationToDelete.setDiscriminator(InvitationType.FRIENDSHIP);
        invitationToDelete.setPlayerSource(target2);
        invitationToDelete.setPlayerTarget(source);
        invitationToDelete.setIsAccepted(true);

    }

    @Test
    @WithMockUser("user1")
    void shouldReturnInvitationsSentByPlayerSource() throws Exception{
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.playerService.findPlayerByUser(user1.getId())).thenReturn(source);
        when(this.invitationService.findAllInvitationForPlayerSource(source.getId())).thenReturn(invitaciones);
        mockMvc.perform(get(BASE_URL + "/sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.[0].playerSource.firstName").value(source.getFirstName()))
                .andExpect(jsonPath("$.[1].playerSource.firstName").value(source.getFirstName()));

    }

    @Test
    @WithMockUser("user1")
    void shouldReturnInvitationsReceivedByPlayerSource() throws Exception {
        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.playerService.findPlayerByUser(user1.getId())).thenReturn(source);
        when(this.invitationService.findAllInvitationForPlayerTarget(source.getId())).thenReturn(invitationsForSource);
        mockMvc.perform(get(BASE_URL + "/received"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].playerTarget.firstName").value(source.getFirstName()));
    }

    @Test
    @WithMockUser("user1")
    public void shouldCreateInvitation() throws Exception {

        Invitation newInvitation = new Invitation();
        newInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        newInvitation.setPlayerSource(target2);
        newInvitation.setPlayerTarget(target1);
        newInvitation.setIsAccepted(false);

        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.playerService.findPlayerByUser(user1.getId())).thenReturn(target2);
        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInvitation))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("user1")
    void shouldNotCreateInvitation() throws Exception{

        Invitation newInvitation = new Invitation();
        newInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        // newInvitation.setPlayerSource(target2);
        newInvitation.setPlayerTarget(target1);

        when(this.userService.findCurrentUser()).thenReturn(user1);
        when(this.playerService.findPlayerByUser(user1.getId())).thenReturn(target1);
        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInvitation))).andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser("user1")
    void shouldAcceptInvitation() throws Exception{

        Invitation newInvitation = new Invitation();
        newInvitation.setId(2);
        newInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        newInvitation.setPlayerSource(target2);
        newInvitation.setPlayerTarget(target1);
        newInvitation.setIsAccepted(true);

        when(this.invitationService.acceptInvitation(newInvitation.getId())).thenReturn(newInvitation);
        mockMvc.perform(put(BASE_URL + "/accept/{id}", newInvitation.getId()).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newInvitation))).andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser("user1")
    void shouldDeleteInvitation() throws Exception{

        when(this.invitationService.findInvitationById(invitationToDelete.getId())).thenReturn(invitationToDelete);
 
        doNothing().when(this.invitationService).deleteInvitation(invitationToDelete.getId());
        mockMvc.perform(delete(BASE_URL + "/{id}", invitationToDelete.getId()).with(csrf()))
                .andExpect(status().isOk());
        
    }
}