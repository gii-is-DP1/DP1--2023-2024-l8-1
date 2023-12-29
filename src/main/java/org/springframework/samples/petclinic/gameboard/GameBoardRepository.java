package org.springframework.samples.petclinic.gameboard;

import org.springframework.data.repository.CrudRepository;

public interface GameBoardRepository extends CrudRepository<GameBoard, Integer> {
}
