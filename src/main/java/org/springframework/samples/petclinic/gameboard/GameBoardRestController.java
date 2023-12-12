package org.springframework.samples.petclinic.gameboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.sector.SectorService;
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
    private HexService hexService;
    
    @Autowired
    public GameBoardRestController(GameBoardService gameBoardService, HexService hexService){
        this.gameBoardService = gameBoardService;
        this.hexService = hexService;
    }

    @GetMapping("/{name}")
    public List<Hex> getGameBoard(@PathVariable("name") String name){
        GameBoard gb = gameBoardService.genRandomGameBoard(name);
        return gameBoardService.getGameBoardHexs(gb);
    }
    
}