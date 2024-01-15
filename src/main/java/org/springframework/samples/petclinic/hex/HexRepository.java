package org.springframework.samples.petclinic.hex;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.ship.Ship;

public interface HexRepository extends CrudRepository<Hex, Integer> {

    @Query("SELECT h FROM Game g " +
            "JOIN g.gameBoard gb " +
            "JOIN gb.sectors s " + 
            "JOIN s.hexs h " +
            "WHERE h.position = :position AND g.name = :name")
    public Hex findHexByPositionInGame(Integer position, String name);

    @Query("SELECT p FROM Ship s JOIN s.player p WHERE s.hex.id = :id")
    Optional<Player> findPlayerInHex(Integer id);

    @Query("SELECT s FROM Ship s JOIN s.hex h WHERE h.id = :id")
    List<Ship> findShipsInHex(Integer id);
    
}
