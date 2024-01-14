package org.springframework.samples.petclinic.round;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RoundRepository extends CrudRepository<Round, Integer> {
    List<Round> findAll();
    
}
