package org.springframework.samples.petclinic.round;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoundService {

    private RoundRepository roundRepository;
    private PhaseService phaseService;

    @Autowired
    public RoundService(RoundRepository roundRepository, PhaseService phaseService){
        this.roundRepository = roundRepository;
        this.phaseService = phaseService;
    }

    @Transactional(readOnly = true)
    public Round findById(int id){
        Optional<Round> result = roundRepository.findById(id);
        return result.isPresent()?result.get():null; 
    }

    @Transactional
    public void roundIsOver(Round round, Phase phase, Game game){
        if (phase.getTurns().stream().allMatch(s -> s.getIsOver())){
            phase.setIsOver(true);
            phaseService.savePhase(phase);
            if (phase.getIsPoint()){
                
            }
        }
        if (round.getPhases().stream().allMatch(s -> s.getIsOver())){
            round.setIsOver(true);
            saveRound(round);
        }
    }

    @Transactional
    public Round saveRound(Round round){
        return roundRepository.save(round);
    }



}
