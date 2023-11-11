package org.springframework.samples.petclinic.invitation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Invitation findInvitationById(int id){
        return ir.findById(id).orElseThrow(()-> new ResourceNotFoundException("Invitation", "ID", id));
    }

    @Transactional(readOnly = true)
    public List<Invitation> findAllInvitationByPlayer(int playerId){
        return ir.findInvitationsForPlayer(playerId);
    }

    @Transactional(readOnly = true)
    public InvitationType findInvitationType(int id){
        return ir.typeInvitation(id);
    }

    @Transactional
    public Invitation saveInvitation(Invitation invitation){
        ir.save(invitation);
        return invitation;
    }

    @Transactional
    public void deleteInvitation(int id){
        Invitation toDelete = findInvitationById(id);
        ir.delete(toDelete);
    }
    
}
