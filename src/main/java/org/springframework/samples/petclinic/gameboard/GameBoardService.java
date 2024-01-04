package org.springframework.samples.petclinic.gameboard;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
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

    @Transactional(readOnly = true)
    public GameBoard getById(int id){
        Optional<GameBoard> result = gameBoardRepository.findById(id);
        return result.isPresent()?result.get():null;
    }

    @Transactional(readOnly = true)
    public GameBoard getGameBoardByGame(String name){
        Game game = gameService.findByName(name);
        return game.getGameBoard() == null ? null : getById(game.getGameBoard().getId());
    }

    @Transactional
    public GameBoard genRandomGameBoard(String game){
        GameBoard newBoard = new GameBoard();
        List<Sector> aux = new ArrayList<>();

        try {
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

        for(int i=0; i<aux.size() ;i++) {
            List<Hex> hexs = aux.get(i).getHexs();
            for(int j=0; j<hexs.size(); j++) {
                Hex hex = hexs.get(j);
                List<Hex> vecinos = hexService.listAdyacencias(hex, game);
                hex.setAdyacentes(vecinos);
            }
        }
        
        aux.add(sectorService.genTriPrime());

        newBoard.setSectors(aux);
        gameBoardRepository.save(newBoard);

        (gameService.findByName(game)).setGameBoard(newBoard);
        gameService.saveGame(gameService.findByName(game));
        
        } catch (Exception e) {
            throw new GameBoardGenerationException("Unexpected error generating game board for game: " + game, e);
        }
        
        return newBoard;
    }
    
}
