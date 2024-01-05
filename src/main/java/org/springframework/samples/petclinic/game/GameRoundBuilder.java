package org.springframework.samples.petclinic.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.round.RoundService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnService;

public interface GameRoundBuilder {
    
    void reset();

    void setRoundPhases(Boolean inicial, List<Player> players);
}

class RoundBuilder implements GameRoundBuilder{
      
    private Round round;
    private RoundService roundService;
    private PhaseService phaseService;
    private TurnService turnService;
    private Random random = new Random();

    public RoundBuilder(RoundService roundService, PhaseService phaseService, TurnService turnService){
        this.roundService = roundService;
        this.phaseService = phaseService;
        this.turnService = turnService;
        this.reset();
    }

    public void reset(){
        this.round = new Round();
    }

    public void setRoundPhases(Boolean inicial, List<Player> players){
        List<Phase> phases = new ArrayList<>();
        if (inicial){
            int primerJugador = random.nextInt(3);
            Phase phase1 = setPhase(primerJugador, true, players);
            phases.add(phase1);
            phases.add(setPhase((primerJugador-1 < 0?2:primerJugador-1), false, players));
        }
        round.setPhases(phases);
    }

    private Phase setPhase(int primerJugador, Boolean clockwise, List<Player> players){
        Phase phase = new Phase();
        List<Turn> turns = new ArrayList<>();
        if (clockwise){
            for (int i = 0; i < 3; i++){
                if(primerJugador+i > 2){
                    primerJugador = 0;
                }else{
                    primerJugador = primerJugador + i;
                }
                //primerJugador = primerJugador+i == 3?0:primerJugador+i;
                turns.add(setTurn((primerJugador), players));
            }
        }else {
            for (int i = 0; i < 3; i++){
                if(primerJugador-i < 0){
                    primerJugador = 2;
                }else{
                    primerJugador = primerJugador - i;
                }
                //primerJugador = primerJugador-i == -1?2:primerJugador-i;
                turns.add(setTurn((primerJugador), players));
            }
        }
        phase.setTurns(turns);
        return phaseService.savePhase(phase);

    }

    private Turn setTurn(int jugador, List<Player> players){
        Turn turn = new Turn();
        turn.setPlayer(players.get(jugador));
        return turnService.saveTurn(turn);
    }

    public Round getRound(){
        Round buildedRound = roundService.saveRound(round);
        this.reset();
        return buildedRound; 
    }
}

class Director {
    private RoundBuilder builder;
    private List<Player> players;

    public Director(RoundBuilder builder, List<Player> players){
        this.builder = builder;
        this.players = players;
    }

    public void InitialRound() {
        builder.reset();
        builder.setRoundPhases(true, players);
    }
}