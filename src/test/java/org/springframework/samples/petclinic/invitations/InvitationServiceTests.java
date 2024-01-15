package org.springframework.samples.petclinic.invitations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.invitation.Invitation;
import org.springframework.samples.petclinic.invitation.InvitationService;
import org.springframework.samples.petclinic.invitation.InvitationType;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class InvitationServiceTests {

    @Autowired
    InvitationService invitationService;

    @Autowired
    PlayerService playerService;

    @Test
    public void shouldFindSingleInvitation() {
        Invitation invitation = invitationService.findInvitationById(1);
        assertFalse(invitation.getIsAccepted());
        assertEquals(4, invitation.getPlayerSource().getId());
        assertEquals(5, invitation.getPlayerTarget().getId());
        assertEquals(InvitationType.FRIENDSHIP, invitation.getDiscriminator());
    }

    @Test
    public void shouldNotFindSingleGameWithBadID() {
        assertThrows(ResourceNotFoundException.class, () -> this.invitationService.findInvitationById(300));
    }

    @Test
    public void shouldFindInvitationsForPlayerTarget() {

        List<Invitation> invitations = invitationService.findAllInvitationForPlayerTarget(6);

        Invitation invitation = EntityUtils.getById(invitations, Invitation.class, 2);
        assertEquals(InvitationType.FRIENDSHIP, invitation.getDiscriminator());
        assertEquals(4, invitation.getPlayerSource().getId());

        Invitation invitation2 = EntityUtils.getById(invitations, Invitation.class, 6);
        assertEquals(InvitationType.GAME, invitation2.getDiscriminator());
        assertEquals(5, invitation2.getPlayerSource().getId());
        assertEquals(5, invitation2.getGame().getId());

    }

    @Test
    public void shouldNotFindInvitationsForNotExistingPlayerTarget() {

        List<Invitation> invitations = invitationService.findAllInvitationForPlayerTarget(300);
        assertEquals(0, invitations.size());

    }

    @Test
    public void shoudlFindInvitationsForPlayerSource() {

        List<Invitation> invitations = invitationService.findAllInvitationForPlayerSource(4);

        Invitation invitation = EntityUtils.getById(invitations, Invitation.class, 1);
        assertEquals(InvitationType.FRIENDSHIP, invitation.getDiscriminator());
        assertEquals(5, invitation.getPlayerTarget().getId());

        Invitation invitation2 = EntityUtils.getById(invitations, Invitation.class, 3);
        assertEquals(InvitationType.FRIENDSHIP, invitation2.getDiscriminator());
        assertEquals(1, invitation2.getPlayerTarget().getId());

    }

    @Test
    public void shoudlNotFindInvitationsForNotExistingPlayerSource() {

        List<Invitation> invitations = invitationService.findAllInvitationForPlayerSource(300);
        assertEquals(0, invitations.size());

    }

    @Test
    public void shoudlAcceptInvitation() {

        Invitation invitation = invitationService.findInvitationById(1);
        invitationService.acceptInvitation(invitation.getId());
        assertTrue(invitation.getIsAccepted());

    }

    @Test
    public void shouldFindInvitationType() {

        Invitation invitation = invitationService.findInvitationById(2);
        InvitationType invitationType = invitationService.findInvitationType(invitation.getId());
        assertEquals(invitation.getDiscriminator(), invitationType);

    }

    @Test
    public void shouldSaveInvitationSuccessfully() {

        List<Invitation> oldInvitations = invitationService.findAllInvitations();
        invitationService.saveInvitation(createValidInvitation());
        List<Invitation> newInvitations = invitationService.findAllInvitations();
        assertEquals(oldInvitations.size() + 1, newInvitations.size());
    }

    private Invitation createValidInvitation() {
        Invitation invitation = new Invitation();

        invitation.setIsAccepted(false);
        invitation.setPlayerSource(playerService.findPlayerById(4));
        invitation.setPlayerTarget(playerService.findPlayerById(5));
        invitation.setGame(null);
        invitation.setDiscriminator(InvitationType.FRIENDSHIP);

        return invitation;
    }

    @Test
    public void shouldNotSaveInvitationIfTheyAreAlreadyFriends() {

        assertThrows(BadRequestException.class, () -> invitationService.saveInvitation(createInvitationForFriends()));

    }

    private Invitation createInvitationForFriends() {

        Invitation invitation = new Invitation();

        invitation.setIsAccepted(false);
        invitation.setPlayerSource(playerService.findPlayerById(1));
        invitation.setPlayerTarget(playerService.findPlayerById(2));
        invitation.setGame(null);
        invitation.setDiscriminator(InvitationType.FRIENDSHIP);

        return invitation;

    }

    @Test
    public void shouldDeleteExistingInvitation() {

        List<Invitation> invitations = invitationService.findAllInvitations();
        Integer invitationsSize = invitations.size();

        invitationService.deleteInvitation(invitations.get(0).getId());

        List<Invitation> updatedInvitations = invitationService.findAllInvitations();
        assertEquals(invitationsSize - 1, updatedInvitations.size());

    }

}
