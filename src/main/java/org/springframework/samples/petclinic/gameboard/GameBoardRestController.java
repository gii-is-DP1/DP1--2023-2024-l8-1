package org.springframework.samples.petclinic.gameboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/gameBoard")
@SecurityRequirement(name = "bearerAuth")
public class GameBoardRestController {

    private GameBoardService gameBoardService;
    
    @Autowired
    public GameBoardRestController(GameBoardService gameBoardService){
        this.gameBoardService = gameBoardService;
    }

    @GetMapping("/{name}")
    public GameBoard getGameBoard(@PathVariable("name") String name){
        return gameBoardService.genRandomGameBoard(name);
    }
    
}
