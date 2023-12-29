package org.springframework.samples.petclinic.ship;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ShipRepository extends CrudRepository<Ship, Integer> {

    @Query("SELECT s FROM Ship s WHERE s.player.id = :playerId AND s.state = 'IN_SUPPLY' ORDER BY s.id ASC")
    List<Ship> findTopXShipsInSupplyState(Integer playerId);
    
}