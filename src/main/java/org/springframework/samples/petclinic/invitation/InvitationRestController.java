package org.springframework.samples.petclinic.invitation;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invitations")
@SecurityRequirement(name = "bearerAuth")
public class InvitationRestController {

    private final InvitationService is;

    @Autowired
    public InvitationRestController(InvitationService is){
        this.is=is;
    }

    @GetMapping
    public List<Invitation> getInvitations(int playerId){
        return is.findAllInvitationByPlayer(playerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Invitation> createInvitation(@RequestBody @Valid Invitation i){
        Invitation newInvitation = new Invitation();
        BeanUtils.copyProperties(i,newInvitation,"id");
        Invitation savedInvitation=is.saveInvitation(newInvitation);
        return new ResponseEntity<>(savedInvitation,HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> deleteInvitation(@PathVariable("id") int id){
        RestPreconditions.checkNotNull(is.findInvitationById(id), "Invitation", "ID", id);
        is.deleteInvitation(id);
        return new ResponseEntity<>(new MessageResponse("Invitation deleted"),HttpStatus.OK);
    } 
    
}
