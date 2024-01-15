package org.springframework.samples.petclinic.gameBoard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.game.GameState;
import org.springframework.samples.petclinic.gameboard.GameBoard;
import org.springframework.samples.petclinic.gameboard.GameBoardService;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class GameBoardServiceTests {
    
    private GameBoardService gameBoardService;
    private GameService gameService;
    private PlayerService playerService;

    @Autowired
    public GameBoardServiceTests(GameBoardService gameBoardService, GameService gameService, PlayerService playerService){
        this.gameBoardService = gameBoardService;
        this.gameService = gameService;
        this.playerService = playerService;
    }
    
    public GameBoard createGameBoard(int n){
        GameBoard gb = new GameBoard();
        List<Sector> ls = new ArrayList<>();
        for (int i = 0; i<n; i++){
            Sector s = new Sector();
            ls.add(s);
        }
        gb.setSectors(ls);
        return gb;
        
    }

    @Transactional
    public Game createGameWBoard(){
        Game g = new Game();
        g.setHost(this.playerService.findPlayerById(1));
        g.setId(22);
        g.setName("partida1");
        g.setPublica(true);
        g.setStartTime(LocalDateTime.of(2023, 11, 11, 11, 11, 11));
        g.setState(GameState.IN_PROGRESS);
        g.setGameBoard(this.gameBoardService.save(createGameBoard(7)));
        return this.gameService.saveGame(g);
    }

    @Transactional
    public Game createGameWOBoard(){
        Game g = new Game();
        g.setHost(this.playerService.findPlayerById(2));
        g.setId(23);
        g.setName("partida2");
        g.setPublica(true);
        g.setStartTime(LocalDateTime.of(2023, 11, 11, 11, 11, 11));
        g.setState(GameState.LOBBY);
        return this.gameService.saveGame(g);
    }

    @Test
    public void shouldCreateValidGameBoard(){
        Game g = this.gameService.getById(1);
        GameBoard gb = this.gameBoardService.genRandomGameBoard(g.getName());
        long triPrime = gb.getSectors().stream().filter(s -> s.getIsTriPrime()).count();
        assertEquals(7, gb.getSectors().size());
        assertEquals(1, triPrime);
    }

    @Test
    public void correctNumberOfHexes(){
        Game g = this.gameService.getById(2);
        GameBoard gb = this.gameBoardService.genRandomGameBoard(g.getName());
        int nHex = this.gameBoardService.getGameBoardHexs(gb).size();
        assertEquals(43, nHex);
    }

    @Test 
    public void shouldInsertGameBoard(){
        int found = this.gameBoardService.findAll().size();
        GameBoard gb = createGameBoard(7);
        this.gameBoardService.save(gb);
        int finalFound = this.gameBoardService.findAll().size();
        assertEquals(found + 1, finalFound);
    }

    @Test 
    public void shouldNotInsertGameBoard(){
        GameBoard gb = createGameBoard(5);
        assertThrows(TransactionSystemException.class, () -> this.gameBoardService.save(gb));
    }

    @Test
    public void correctlyFindsById(){
        int id1 = this.gameBoardService.save(createGameBoard(7)).getId();
        int id2 = this.gameBoardService.getById(id1).getId();
        assertEquals(id1, id2);
    }

    @Test
    public void shouldFindCorrectBoardByGameName(){
        Game g = createGameWBoard();
        int id1 = g.getGameBoard().getId();
        int id2 = this.gameBoardService.getGameBoardByGame(g.getName()).getId();
        assertEquals(id1, id2);
    }

    @Test
    public void shouldNotFindBoardByGameName(){
        Game g = createGameWOBoard();
        GameBoard gb = this.gameBoardService.getGameBoardByGame(g.getName());
        assertEquals(null, gb);
    }

}
