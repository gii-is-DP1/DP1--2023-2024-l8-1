package org.springframework.samples.petclinic.gameboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.exceptions.AccessDeniedException;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/gameBoard")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Game Boards", description = "The GameBoard managemet API")
public class GameBoardRestController {

    private final GameBoardService gameBoardService;
    private final GameService gameService;
    private final UserService userService;
    
    @Autowired
    public GameBoardRestController(GameBoardService gameBoardService,
                GameService gameService, UserService userService){
        this.gameBoardService = gameBoardService;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Hex>> getGameBoard(@PathVariable("name") String name){
        Player aux = userService.findPlayerByUser(userService.findCurrentUser().getId());
        Game game = gameService.findByName(name);
        if (gameBoardService.getGameBoardByGame(name) == null){
            if (aux != game.getHost()){
                throw new AccessDeniedException("Esta partida no está empezada");
            }else{   
                gameBoardService.genRandomGameBoard(name);
            }
        }
        
        GameBoard gb = gameBoardService.getGameBoardByGame(name);
        return new ResponseEntity<List<Hex>>(gameBoardService.getGameBoardHexs(gb), HttpStatus.OK);  
        
    }
}
