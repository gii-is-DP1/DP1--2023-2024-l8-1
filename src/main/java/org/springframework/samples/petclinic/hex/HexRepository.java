package org.springframework.samples.petclinic.hex;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HexRepository extends CrudRepository<Hex, Integer> {

    @Query("SELECT h FROM Game g " +
            "JOIN g.gameBoard gb " +
            "JOIN gb.sectors s " + 
            "JOIN s.hexs h " +
            "WHERE h.position = :position AND g.name = :name")
    public Hex findHexByPosition(Integer position, String name);
    
}
