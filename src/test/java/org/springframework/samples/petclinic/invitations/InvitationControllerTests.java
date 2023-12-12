package org.springframework.samples.petclinic.invitations;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.invitation.Invitation;
import org.springframework.samples.petclinic.invitation.InvitationRestController;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.invitation.InvitationType;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRol;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private static final int TEST_USER_ID = 1;
    private static final int TEST_PLAYER1_ID = 1;
    private static final int TEST_PLAYER2_ID = 1;
    private static final int TEST_INVITATION_ID = 1;
    private static final String BASE_URL = "/api/v1/invitations";
    private static final String INVITATIONS_SENT_URL = "/api/v1/invitations/sent";
    private static final String INVITATIONS_RECEIVED_URL = "/api/v1/invitations/received";
    private static final String ACCEPT_INVITATION_URL = "/api/v1/invitations/accept/" + TEST_INVITATION_ID;
    private static final Integer TEST_PLAYER_1_USER_ID = 1;
    private static final Integer TEST_PLAYER_2_USER_ID = 2;

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
    MockMvc mockMvc; // Objeto proporcionado por Spring para realizar solicitudes HTTP simuladas y
                             // realizar aserciones sobre las respuestas

    private User logged;
    private Player player1;
    private Player player2;
    private User player1User;
    private User player2User;
    private Invitation invitation;

    @BeforeEach
    void setUp() {

        // Configuramos los objetos necesarios para las pruebas

        Authorities playerAuth = new Authorities();
        playerAuth.setId(1);
        playerAuth.setAuthority("PLAYER");

        /*
         * player1User = new User();
         * player1User.setId(TEST_PLAYER_1_USER_ID);
         * player1User.setUsername("player1Test");
         * player1User.setPassword("player1Test");
         * player1User.setAuthority(playerAuth);
         */

        player2User = new User();
        player2User.setId(TEST_PLAYER_2_USER_ID);
        player2User.setUsername("player2Test");
        player2User.setPassword("player2Test");
        player2User.setAuthority(playerAuth);

        /*
         * player1 = new Player();
         * player1.setId(TEST_PLAYER1_ID);
         * player1.setFirstName("Jugador Uno");
         * player1.setLastName("Jugador Uno");
         * player1.setNumCards(3);
         * player1.setNumShips(15);
         * player1.setScore(0);
         * player1.setRol(PlayerRol.HOST);
         * player1.setStartPlayer(false);
         * player1.setFriends(null);
         * player1.setUser(player1User);
         */

        player2 = new Player();
        player2.setId(TEST_PLAYER2_ID);
        player2.setFirstName("Jugador Dos");
        player2.setLastName("Jugador Dos");
        player2.setNumCards(3);
        player2.setNumShips(15);
        player2.setScore(0);
        player2.setRol(PlayerRol.GUEST);
        player2.setStartPlayer(false);
        player2.setFriends(null);
        player2.setUser(player2User);

        /*
         * invitation = new Invitation();
         * invitation.setId(TEST_INVITATION_ID);
         * invitation.setGame(null);
         * invitation.setDiscriminator(InvitationType.FRIENDSHIP);
         * invitation.setIsAccepted(false);
         * invitation.setPlayerSource(player1);
         * invitation.setPlayerTarget(player2);
         */

        // Comportamiento esperado del userService
        when(this.userService.findCurrentUser()).thenReturn(getUserFromDetails(
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
    }

    // Con este método convertimos UserDetails un objeto User simulado
    private User getUserFromDetails(UserDetails details) {
        logged = new User();
        logged.setUsername(details.getUsername());
        logged.setPassword(details.getPassword());
        Authorities aux = new Authorities();
        for (GrantedAuthority auth : details.getAuthorities()) {
            aux.setAuthority(auth.getAuthority());
        }
        logged.setAuthority(aux);
        return logged;
    }

    // H3+E1: Solicitud de amistad a player2
    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void createInvitationShouldReturnCreatedStatus() throws Exception {

        // Simula el jugador origen en la sesion
        Player playerSource = new Player();
        when(playerService.findPlayerByUser(logged.getId())).thenReturn(playerSource);

        // Configura el comportamiento esperado del servicio de invitación
        Invitation tesInvitation = new Invitation();
        tesInvitation.setGame(null);
        tesInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        tesInvitation.setIsAccepted(false);
        tesInvitation.setId(1);
        tesInvitation.setPlayerSource(playerSource);
        tesInvitation.setPlayerTarget(player2); // Ajusta según tus necesidades

        reset(invitationService);
        when(invitationService.saveInvitation(any(Invitation.class))).thenReturn(tesInvitation);

        // Realiza una solicitud HTTP simulada al controlador para crear una invitación
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tesInvitation)))
                .andExpect(status().isCreated());

        // Comprobamos que se ha intentado grabar el juego en la bd:
        verify(invitationService, times(1)).saveInvitation(any(Invitation.class));

    }

    // H3-E1: Solicitud de amistad a un usuario inexistente

    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void createInvitationShouldReturnBadRequestForNonExistentUser() throws Exception {

        // Simula el jugador origen en la sesion
        Player playerSource = new Player();
        when(playerService.findPlayerByUser(logged.getId())).thenReturn(playerSource);

        // Configura el comportamiento esperado del servicio de invitación
        Invitation tesInvitation = new Invitation();
        tesInvitation.setGame(null);
        tesInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        tesInvitation.setIsAccepted(false);
        tesInvitation.setId(1);

        // Simula un jugador destino que no existe
        // when(playerService.findPlayerByUser(5)).thenReturn(null);
        tesInvitation.setPlayerSource(playerSource);
        tesInvitation.setPlayerTarget(null); // Así ya vale para crear un player que no existe?

        reset(invitationService);
        when(invitationService.saveInvitation(any(Invitation.class))).thenReturn(tesInvitation);

        // Realiza una solicitud HTTP simulada al controlador para crear una invitación
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tesInvitation)))
                .andExpect(status().isBadRequest());

        // Comprobamos que se ha intentado grabar el juego en la bd:
        verify(invitationService, never()).saveInvitation(any(Invitation.class));

    }

    // H3-E2: Solicitud de amistad de un usuario ya solicitado
    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void createInvitationShouldReturnBadRequestForExistingFriend() throws Exception {
        // Simula el jugador origen en la sesión
        Player playerSource = new Player();
        when(playerService.findPlayerByUser(logged.getId())).thenReturn(playerSource);

        // Configura el comportamiento esperado del servicio de invitación
        Invitation tesInvitation = new Invitation();
        tesInvitation.setGame(null);
        tesInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        tesInvitation.setIsAccepted(false);
        tesInvitation.setId(1);

        // Simula un jugador destino que ya es amigo
        when(playerService.findPlayerByUser(2)).thenReturn(player2);
        playerSource.getFriends().add(player2);
        tesInvitation.setPlayerSource(playerSource);
        tesInvitation.setPlayerTarget(player2); // Ajusta según tus necesidades

        reset(invitationService);
        when(invitationService.saveInvitation(any(Invitation.class))).thenReturn(tesInvitation);

        // Realiza una solicitud HTTP simulada al controlador para crear una invitación
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tesInvitation)))
                .andExpect(status().isBadRequest());

        // Comprobamos que no se ha intentado grabar la invitación en la BD
        verify(invitationService, never()).saveInvitation(any(Invitation.class));
    }

    // H3-E3: Solicitud de amistad de mi propio usuario
    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void createInvitationShouldReturnBadRequestForSelf() throws Exception {
        // Simula el jugador origen en la sesión
        Player playerSource = new Player();
        when(playerService.findPlayerByUser(logged.getId())).thenReturn(playerSource);

        // Configura el comportamiento esperado del servicio de invitación
        Invitation tesInvitation = new Invitation();
        tesInvitation.setGame(null);
        tesInvitation.setDiscriminator(InvitationType.FRIENDSHIP);
        tesInvitation.setIsAccepted(false);
        tesInvitation.setId(1);

        // Simula un jugador destino que es el mismo que el jugador origen
        tesInvitation.setPlayerSource(playerSource);
        tesInvitation.setPlayerTarget(playerSource); // Ajusta según tus necesidades

        reset(invitationService);
        when(invitationService.saveInvitation(any(Invitation.class))).thenReturn(tesInvitation);

        // Realiza una solicitud HTTP simulada al controlador para crear una invitación
        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tesInvitation)))
                .andExpect(status().isBadRequest());

        // Comprobamos que no se ha intentado grabar la invitación en la BD
        verify(invitationService, never()).saveInvitation(any(Invitation.class));
    }
/*
 *     // H4+E1: Aceptar solicitud de amistad de manubrioh03
    @Test
    @WithMockUser(username = "player1", authorities = "PLAYER")
    public void acceptFriendshipRequestShouldAddUserToFriendsList() throws Exception {
        // Simular la solicitud de amistad entrante
        Invitation friendshipRequest = createFriendshipRequest("manubrioh03");

        // Configurar el comportamiento esperado del servicio de invitación
        when(invitationService.findInvitationById(friendshipRequest.getId())).thenReturn(friendshipRequest);
        when(playerService.findPlayerByUser(logged.getId())).thenReturn(createPlayer("jugador1"));
        when(playerService.findPlayerByUserName("manubrioh03")).thenReturn(createPlayer("manubrioh03"));

        // Realizar una solicitud HTTP simulada para aceptar la solicitud de amistad
        mockMvc.perform(post(ACCEPT_INVITATION_URL, friendshipRequest.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que la solicitud de amistad ha sido aceptada y el usuario se ha
        // agregado a la lista de amigos
        verify(invitationService, times(1)).acceptFriendshipInvitation(friendshipRequest.getId());
        verify(playerService, times(1)).addFriend(any(Player.class), any(Player.class));
    }

    // H4-E1: Rechazar solicitud de amistad de manubrioh03
    @Test
    @WithMockUser(username = "jugador1", authorities = "PLAYER")
    public void rejectFriendshipRequestShouldNotAddUserToFriendsList() throws Exception {
        // Simular la solicitud de amistad entrante
        Invitation friendshipRequest = createFriendshipRequest("manubrioh03");

        // Configurar el comportamiento esperado del servicio de invitación
        when(invitationService.findInvitationById(friendshipRequest.getId())).thenReturn(friendshipRequest);
        when(playerService.findPlayerByUser(logged.getId())).thenReturn(createPlayer("jugador1"));
        when(playerService.findPlayerByUserName("manubrioh03")).thenReturn(createPlayer("manubrioh03"));

        // Realizar una solicitud HTTP simulada para rechazar la solicitud de amistad
        mockMvc.perform(post(REJECT_INVITATION_URL, friendshipRequest.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que la solicitud de amistad ha sido rechazada y el usuario no se ha
        // agregado a la lista de amigos
        verify(invitationService, times(1)).rejectFriendshipInvitation(friendshipRequest.getId());
        verify(playerService, never()).addFriend(any(Player.class), any(Player.class));
    }
 */

}