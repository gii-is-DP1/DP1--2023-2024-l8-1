package org.springframework.samples.petclinic.gameboard;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface GameBoardRepository extends CrudRepository<GameBoard, Integer> {
    
    List<GameBoard> findAll();
}
