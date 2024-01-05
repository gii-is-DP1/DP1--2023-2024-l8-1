package org.springframework.samples.petclinic.invitation;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/invitations")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Invitations", description = "The Invitation managemet API")
public class InvitationRestController {

    private final InvitationService is;
    private final PlayerService ps;
    private final UserService us;

    @Autowired
    public InvitationRestController(InvitationService is,UserService us, PlayerService ps){
        this.is=is;
        this.ps=ps;
        this.us=us;
    }

    @GetMapping("/sent")
    public List<Invitation> getInvitationsSent(){
        Integer currentPlayerId = ps.findPlayerByUser(us.findCurrentUser().getId()).getId();
        return is.findAllInvitationForPlayerSource(currentPlayerId);
    }

    @GetMapping("/received")
    public List<Invitation> getInvitationsReceived(){
        Integer currentPlayerId = ps.findPlayerByUser(us.findCurrentUser().getId()).getId();
        return is.findAllInvitationForPlayerTarget(currentPlayerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Invitation> createInvitation(@RequestBody @Valid Invitation i){
        Invitation newInvitation = new Invitation();
        BeanUtils.copyProperties(i,newInvitation,"id");

        newInvitation.setPlayerSource(ps.findPlayerByUser(us.findCurrentUser().getId()));
        
        if(newInvitation.getPlayerSource().equals(newInvitation.getPlayerTarget()))
            throw new BadRequestException("No se puede enviar una invitación a uno mismo");
        Invitation savedInvitation=is.saveInvitation(newInvitation);
        return new ResponseEntity<>(savedInvitation,HttpStatus.CREATED);
    }


    @PutMapping("/accept/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Invitation> acceptInvitation(@PathVariable("id") int id){
        is.acceptInvitation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> deleteInvitation(@PathVariable("id") int id){
        RestPreconditions.checkNotNull(is.findInvitationById(id), "Invitation", "ID", id);
        is.deleteInvitation(id);
        return new ResponseEntity<>(new MessageResponse("Invitation deleted"),HttpStatus.OK);
    } 
    
}
