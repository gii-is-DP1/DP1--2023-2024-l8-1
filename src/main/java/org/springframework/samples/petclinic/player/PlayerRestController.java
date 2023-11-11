package org.springframework.samples.petclinic.player;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.user.User;
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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
@SecurityRequirement(name = "bearerAuth")
public class PlayerRestController {

    private final PlayerService ps;
    private final UserService us;

    public PlayerRestController(PlayerService ps, UserService us){
        this.ps=ps;
        this.us=us;
    }

    @GetMapping
    public ResponseEntity<List<Player>> findAll(){
        return new ResponseEntity<>((List<Player>) ps.findAll(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> findById(@PathVariable("id") int id){
        return new ResponseEntity<>(ps.findPlayerById(id),HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Player> createPlayer(@RequestBody @Valid Player p){
        Player newPlayer=new Player();
        BeanUtils.copyProperties(p,newPlayer,"id");
        User user=us.findCurrentUser();
        newPlayer.setUser(user);
        Player savedPlayer=ps.savePlayer(newPlayer);
        return new ResponseEntity<>(savedPlayer,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") int id,@RequestBody @Valid Player p){
        RestPreconditions.checkNotNull(ps.findPlayerById(id),"Player","ID",id);
        return new ResponseEntity<>(ps.updatePlayer(p, id),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> deletePlayer(@PathVariable("id") int id){
        RestPreconditions.checkNotNull(ps.findPlayerById(id),"Player","ID",id);
        ps.deletePlayer(id);
        return new ResponseEntity<>(new MessageResponse("Player deleted"),HttpStatus.OK);
    }
    
}
