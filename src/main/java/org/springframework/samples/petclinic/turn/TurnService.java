package org.springframework.samples.petclinic.turn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TurnService {
    
    private TurnRepository turnRepository;

    @Autowired
    public TurnService(TurnRepository turnRepository){
        this.turnRepository = turnRepository;
    }

    @Transactional
    public Turn saveTurn(Turn turn){
        return turnRepository.save(turn);
    }

}
