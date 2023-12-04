package org.springframework.samples.petclinic.gameboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.samples.petclinic.sector.SectorService;
import org.springframework.stereotype.Service;


@Service
public class GameBoardService {

    private GameBoardRepository gameBoardRepository;
    private SectorService sectorService;
    private GameService gameService;

    @Autowired
    public GameBoardService(GameBoardRepository gameBoardRepository, 
                                SectorService sectorService, GameService gameService) {

        this.gameBoardRepository = gameBoardRepository;
        this.sectorService=sectorService;
        this.gameService = gameService;
    }

    public GameBoard genRandomGameBoard(String game){
        GameBoard newBoard = new GameBoard();
        List<Sector> aux = new ArrayList<>();

        aux.add(sectorService.genTriPrime());
        for(int i = 0; i < 6; i++){
            aux.add(sectorService.genRandom());
        }
        newBoard.setSectors(aux);
        gameBoardRepository.save(newBoard);

        (gameService.findByName(game)).setGameBoard(newBoard);
        gameService.saveGame(gameService.findByName(game));
        
        return newBoard;
    }
    
}
