package org.springframework.samples.petclinic.turn;

import org.springframework.data.repository.CrudRepository;

public interface TurnRepository extends CrudRepository<Turn, Integer> {
    
}
