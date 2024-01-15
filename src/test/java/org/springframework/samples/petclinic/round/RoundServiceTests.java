package org.springframework.samples.petclinic.round;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameState;
import org.springframework.samples.petclinic.gameboard.GameBoard;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class RoundServiceTests {

    private RoundService roundService;
    private PlayerService playerService;

    @Autowired
    public RoundServiceTests(RoundService roundService, PlayerService playerService){
        this.roundService = roundService;
        this.playerService = playerService;
    }

    @Transactional
    private Phase createValidPhaseToEnd() {
        Phase phase = new Phase();
        List<Turn> ls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Turn t = new Turn();
            t.setIsOver(true);
            ls.add(t);
        }
        phase.setTurns(ls);
        return phase;
    }

    @Transactional
    private Round createValidRound(){
        Round round = new Round();
        List<Phase> ls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Phase p = createValidPhaseToEnd();
            ls.add(p);
        }
        round.setPhases(ls);
        return round;
    }

    @Transactional
    private Round createValidRoundToEnd(){
        Round round = new Round();
        List<Phase> ls = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Phase p = createValidPhaseToEnd();
            p.setIsOver(true);
            ls.add(p);
        }
        ls.add(createValidPhaseToEnd());
        round.setPhases(ls);
        return round;
    }

    @Transactional
    private Game createGameWSectors(){
        Game g = new Game();
        g.setId(22);
        g.setName("partida1");
        g.setPublica(true);
        g.setStartTime(LocalDateTime.of(2023, 11, 11, 11, 11, 11));
        g.setState(GameState.IN_PROGRESS);
        GameBoard gb = new GameBoard();
        List<Sector> ls = new ArrayList<>();
        for (int i = 0; i<7; i++){
            Sector s = new Sector();
            s.setChoosen(true);
            s.setIsTriPrime(false);
            ls.add(s);
        }
        gb.setSectors(ls);
        g.setGameBoard(gb);
        return g;
                
    }

    @Test
    public void correctlyFinishesPhase() {
        Phase p = createValidPhaseToEnd();
        Round r = createValidRound();
        this.roundService.roundIsOver(r, p, null, null);
        assertEquals(true, p.getIsOver());
    }

    @Test
    public void correctlyNotFinishesPhase() {
        Round r = createValidRound();
        Phase p = createValidPhaseToEnd();
        p.getTurns().get(2).setIsOver(false);
        this.roundService.roundIsOver(r, p, null, null);
        assertEquals(false, p.getIsOver());
    }

    @Test
    public void correctlyFinishesRound() {
        Round r = createValidRoundToEnd();
        Phase p = r.getPhases().get(2);
        this.roundService.roundIsOver(r, p, null, null);
        assertEquals(true, r.getIsOver());
    }

    @Test
    public void correctlyNotFinishesRound() {
        Round r = createValidRound();
        Phase p = r.getPhases().get(2);
        this.roundService.roundIsOver(r, p, null, null);
        assertEquals(false, r.getIsOver());
    }

    @Test
    @Transactional
    public void correctlyResetSectors(){
        Round r = createValidRound();
        Phase p = createValidPhaseToEnd();
        p.setIsPoint(true);
        Game g = createGameWSectors();
        this.roundService.roundIsOver(r, p, g, null);
        assertEquals(true, g.getGameBoard().getSectors().stream().allMatch(s -> !s.getIsScored()));
    }

    @Test
    @Transactional
    public void correctlyAddsTriTurn() {
        Phase p = createValidPhaseToEnd();
        int turns = p.getTurns().size();
        Round r = createValidRound();
        p.setIsPoint(true);
        Player player = this.playerService.findPlayerById(1);
        this.roundService.roundIsOver(r, p, null, player);
        assertEquals(turns+1, p.getTurns().size());
    }

    @Test
    @Transactional
    public void shouldInsertRound(){
        int found = this.roundService.findAll().size();
        Round r = createValidRound();
        this.roundService.saveRound(r);
        int finalFound = this.roundService.findAll().size();
        assertEquals(found + 1, finalFound);

    }



}
