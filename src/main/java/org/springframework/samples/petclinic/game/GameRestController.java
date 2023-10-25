package org.springframework.samples.petclinic.game;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.util.RestPreconditions;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/game")
@SecurityRequirement(name = "bearerAuth")
public class GameRestController {
    
    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService){
        this.gameService = gameService;
    }

    @GetMapping
	public ResponseEntity<List<Game>> findAll() {
		return new ResponseEntity<>((List<Game>) gameService.getGames(), HttpStatus.OK);
	}

    @GetMapping("/{id}")
    public ResponseEntity<Game> findGame(@PathVariable("id") int id){
        Game gameToGet=gameService.getById(id);
        if(gameToGet==null)
            throw new ResourceNotFoundException("Game with id "+id+ "not found!");
        return new ResponseEntity<Game>(gameToGet, HttpStatus.OK);    
    }   

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> crateGame(@RequestBody @Valid Game newGame, BindingResult br){
        Game result=null;
        if(!br.hasErrors())
            result=gameService.saveGame(newGame);
        else 
            throw new BadRequestException(br.getAllErrors());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updateGame(@RequestBody @Valid Game newGame, BindingResult br,@PathVariable("id") int id) {
        Game gameToUpdate=this.findGame(id).getBody();
		if(br.hasErrors())
			throw new BadRequestException(br.getAllErrors());		
		else if(newGame.getId()==null || !newGame.getId().equals(id))
			throw new BadRequestException("Achievement id is not consistent with resource URL:"+id);
		else{
			BeanUtils.copyProperties(newGame, gameToUpdate, "id");
			gameService.saveGame(gameToUpdate);
		}			
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable("id") int id){
        findGame(id);
        gameService.deleteGameById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}