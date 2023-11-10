package org.springframework.samples.petclinic.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, String>{
    
    List<Game> findAll();

    Optional<Game> findById(Integer id);


}
