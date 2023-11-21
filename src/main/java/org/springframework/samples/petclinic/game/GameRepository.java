package org.springframework.samples.petclinic.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.player.Player;

public interface GameRepository extends CrudRepository<Game, String>{
    
    List<Game> findAll();

    Optional<Game> findById(Integer id);

    @Query("SELECT g FROM Game g WHERE g.name = :name")
    Optional<Game> findByName(String name);

    @Query("SELECT g.players FROM Game g WHERE g.id = :id")
    List<Player> findGamePlayers(int id);

    @Query("SELECT g FROM Game g WHERE g.publica = true AND g.state = LOBBY")
    List<Game> findPublicas();

}
