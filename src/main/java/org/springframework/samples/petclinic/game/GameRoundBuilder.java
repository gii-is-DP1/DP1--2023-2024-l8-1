package org.springframework.samples.petclinic.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.round.RoundService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnService;

public interface GameRoundBuilder {
    
    void reset();

    void setRoundPhases(Boolean inicial, Boolean isFinal, int playerInicial, List<Player> players);
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

    public void setRoundPhases(Boolean inicial, Boolean isFinal, int playerInicial, List<Player> players){
        List<Phase> phases = new ArrayList<>();
        if (inicial){
            int primerJugador = random.nextInt(3);
            Phase phase1 = setPhaseInicial(primerJugador, true, players);
            phases.add(phase1);
            phases.add(setPhaseInicial((primerJugador-1 < 0?2:primerJugador-1), false, players));
        } else {
            for (int i = 0; i < 3; i++){
                phases.add(setPhase(playerInicial, i, players));
            }
            Phase phase = setPhaseInicial(playerInicial, true, players);
            phase.setIsPoint(true);
            phases.add(phaseService.savePhase(phase));
        }
        round.setIsFinal(isFinal);
        round.setPhases(phases);
    }

    private Phase setPhase(int playerInicial, int number, List<Player> players){
        Phase phase = new Phase();
        List<Turn> turns = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        for (Player player : players){
            cards.add(player.getCards().stream().filter(c -> c.getPerformingOrder() == number).findFirst().get()); 
        }
        //Número de cartas EXPLORE
        List<Card> explore = cards.stream().filter(c -> c.getType() == CardType.EXPLORE).collect(Collectors.toList());
        for (int i = 0; i < players.size(); i++){
                int p2 = playerInicial+i > 2?playerInicial+i-3:playerInicial+i;
                Card card = explore.stream().filter(c -> c.getPlayer().equals(players.get(p2))).findAny().get();
                if (card != null) turns.add(setTurn(card.getPlayer()));
                if (explore.size() == 1) card.setUsesLeft(3);
                if (explore.size() == 2) card.setUsesLeft(2);
                if (explore.size() == 3) card.setUsesLeft(1);
            }

        //Número de cartas EXPAND
        List<Card> expand = cards.stream().filter(c -> c.getType() == CardType.EXPAND).collect(Collectors.toList());
        for (int i = 0; i < players.size(); i++){
                int p2 = playerInicial+i > 2?playerInicial+i-3:playerInicial+i;
                Card card = expand.stream().filter(c -> c.getPlayer().equals(players.get(p2))).findAny().get();
                if (card != null) turns.add(setTurn(card.getPlayer()));
                if (expand.size() == 1) card.setUsesLeft(3);
                if (expand.size() == 2) card.setUsesLeft(2);
                if (expand.size() == 3) card.setUsesLeft(1);
            }

        //Número de cartas EXTERMINATE
        List<Card> exterminate = cards.stream().filter(c -> c.getType() == CardType.EXTERMINATE).collect(Collectors.toList());
        for (int i = 0; i < players.size(); i++){
                int p2 = playerInicial+i > 2?playerInicial+i-3:playerInicial+i;
                Card card = exterminate.stream().filter(c -> c.getPlayer().equals(players.get(p2))).findAny().get();
                if (card != null) turns.add(setTurn(card.getPlayer()));
                if (exterminate.size() == 1) card.setUsesLeft(3);
                if (exterminate.size() == 2) card.setUsesLeft(2);
                if (exterminate.size() == 3) card.setUsesLeft(1);
            }
        
        phase.setTurns(turns);
        return phaseService.savePhase(phase);
    }

    private Phase setPhaseInicial(int primerJugador, Boolean clockwise, List<Player> players){
        Phase phase = new Phase();
        List<Turn> turns = new ArrayList<>();
        if (clockwise){
            for (int i = 0; i < 3; i++){
                turns.add(setTurn(players.get(primerJugador+i > 2?primerJugador+i-3:primerJugador+i)));
            }
        }else {
            for (int i = 0; i < 3; i++){
                turns.add(setTurn(players.get(primerJugador-i < 0?primerJugador-i+3:primerJugador-i)));
            }
        }
        phase.setTurns(turns);
        return phaseService.savePhase(phase);

    }

    private Turn setTurn(Player player){
        Turn turn = new Turn();
        turn.setPlayer(player);
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
    private int playerInicial;

    public Director(RoundBuilder builder, List<Player> players, int playerInicial){
        this.builder = builder;
        this.players = players;
        this.playerInicial = playerInicial;
    }

    public void InitialRound() {
        builder.reset();
        builder.setRoundPhases(true, false, playerInicial, players);
    }

    public void NormalRound(){
        builder.reset();
        builder.setRoundPhases(false, false, playerInicial, players);
    }

    public void FinalRound(){
        builder.reset();
        builder.setRoundPhases(false, true, playerInicial, players);
    }
}