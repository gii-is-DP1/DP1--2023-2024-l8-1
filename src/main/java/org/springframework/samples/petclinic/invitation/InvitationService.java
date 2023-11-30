package org.springframework.samples.petclinic.invitation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InvitationService {

    private InvitationRepository ir;

    @Autowired
    public InvitationService(InvitationRepository ir){
        this.ir=ir;
    }

    @Transactional(readOnly = true)
    public List<Invitation> findAllInvitations() {
        return ir.findAll();
    }

    @Transactional(readOnly = true)
    public Invitation findInvitationById(int id){
        return ir.findById(id).orElseThrow(()-> new ResourceNotFoundException("Invitation", "ID", id));
    }

    // Obtener todas las invitaciones enviadas al jugador con id = palyerId
    @Transactional(readOnly = true)
    public List<Invitation> findAllInvitationForPlayerTarget(int playerId){
        return ir.findInvitationsForPlayerTarget(playerId);
    }

    @Transactional(readOnly = true)
    public List<Invitation> findAllInvitationForPlayerSource(int playerId){
        return ir.findInvitationsForPlayerSource(playerId);
    }

    @Transactional
    public Invitation acceptInvitation(int id){
        Invitation inv = findInvitationById(id);
        inv.setIsAccepted(true);
        return saveInvitation(inv);
    }

    @Transactional(readOnly = true)
    public InvitationType findInvitationType(int id){
        return ir.typeInvitation(id);
    }

    @Transactional
    public Invitation saveInvitation(Invitation invitation) throws DataAccessException{
        ir.save(invitation);
        return invitation;
    }

    @Transactional
    public void deleteInvitation(int id){
        Invitation toDelete = findInvitationById(id);
        ir.delete(toDelete);
    }
    
}
