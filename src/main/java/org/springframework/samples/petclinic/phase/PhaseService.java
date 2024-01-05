package org.springframework.samples.petclinic.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhaseService {
    
    private PhaseRepository phaseRepository;

    @Autowired
    public PhaseService(PhaseRepository phaseRepository){
        this.phaseRepository = phaseRepository;
    }

    @Transactional(readOnly = true)
    public Phase findById(int id){
        Optional<Phase> result = phaseRepository.findById(id);
        return result.isPresent()?result.get():null; 
    }

    @Transactional
    public Phase savePhase(Phase phase){
        return phaseRepository.save(phase);
    }
}


