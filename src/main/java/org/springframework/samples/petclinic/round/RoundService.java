package org.springframework.samples.petclinic.round;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Round saveRound(Round round){
        return roundRepository.save(round);
    }



}
