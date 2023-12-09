package org.springframework.samples.petclinic.gameboard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.samples.petclinic.sector.SectorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GameBoardService {

    private GameBoardRepository gameBoardRepository;
    private SectorService sectorService;
    private GameService gameService;
    private HexService hexService;

    @Autowired
    public GameBoardService(GameBoardRepository gameBoardRepository, 
                                SectorService sectorService, GameService gameService,
                                HexService hexService) {

        this.gameBoardRepository = gameBoardRepository;
        this.sectorService=sectorService;
        this.gameService = gameService;
        this.hexService = hexService;
    }

    @Transactional
    public GameBoard genRandomGameBoard(String game){
        GameBoard newBoard = new GameBoard();
        List<Sector> aux = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            Sector sector = sectorService.genRandom();
            sector.setPosition(i);
            List<Hex> cerdas = sector.getHexs(); 
            for (int j = 0; j < 7; j++){
                Hex hex = cerdas.get(j);
                hex.setPosition(7*i+j);
                hexService.save(hex);
            }
            aux.add(sector);
        }
        
        aux.add(sectorService.genTriPrime());

        newBoard.setSectors(aux);
        gameBoardRepository.save(newBoard);

        (gameService.findByName(game)).setGameBoard(newBoard);
        gameService.saveGame(gameService.findByName(game));
        
        return newBoard;
    }

    @Transactional(readOnly = true)
    public List<Hex> getGameBoardHexs(GameBoard gameBoard){
        List<Hex> aux = new ArrayList<>();
        for (Sector s : gameBoard.getSectors()){
            for (Hex h : s.getHexs()){
                aux.add(h);
            }
        }
        return aux;
    }
    
}
