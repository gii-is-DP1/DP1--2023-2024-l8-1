package org.springframework.samples.petclinic.gameboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GameBoardService {

    private GameBoardRepository gameBoardRepository;

    @Autowired
    public GameBoardService(GameBoardRepository gameBoardRepository) {
        this.gameBoardRepository = gameBoardRepository;
    }
    
}
