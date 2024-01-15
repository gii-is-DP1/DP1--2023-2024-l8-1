package org.springframework.samples.petclinic.player;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.auth.payload.response.MessageResponse;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Players", description = "The Player managemet API")
public class PlayerRestController {

    private final PlayerService ps;
    private final UserService us;
    private final ShipService ss;

    public PlayerRestController(PlayerService ps, UserService us, ShipService ss){
        this.ps=ps;
        this.us=us;
        this.ss=ss;
    }

    @GetMapping
    public ResponseEntity<List<Player>> findAll(){
        return new ResponseEntity<>((List<Player>) ps.findAll(),HttpStatus.OK);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Player>> findFriends(){
        return new ResponseEntity<>((List<Player>) ps.getFriends(),HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Player> getDetails(){
        int id = (us.findPlayerByUser(us.findCurrentUser().getId())).getId();
        return findById(id);
    }

    @PutMapping("/startSpectating/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> startSpectating(@PathVariable("username") String username){
        User user = us.findUser(username);
        Player player = ps.findPlayerByUser(user.getId());
        ps.startSpectating(player);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/remainingShips")
    public Integer getPlayerRemainingShips(@PathVariable("username") String username) {
        User user = us.findUser(username);
        Player player = ps.findPlayerByUser(user.getId());
        List<Ship> playerShips = ss.selectShipsFromSupply(player.getId());
        return playerShips.size();
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

    @PutMapping("/add/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Player> addFriend(@PathVariable("id") int id){
        Player me = us.findPlayerByUser(us.findCurrentUser().getId());
        ps.addFriend(me, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> deleteFriend(@PathVariable("id") int id){
        ps.deleteFriend(id);
        return new ResponseEntity<>(new MessageResponse("Friend deleted"),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> deletePlayer(@PathVariable("id") int id){
        RestPreconditions.checkNotNull(ps.findPlayerById(id),"Player","ID",id);
        ps.deletePlayer(id);
        return new ResponseEntity<>(new MessageResponse("Player deleted"),HttpStatus.OK);
    }
    
}
