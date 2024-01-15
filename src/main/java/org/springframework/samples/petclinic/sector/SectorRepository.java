package org.springframework.samples.petclinic.sector;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SectorRepository extends CrudRepository<Sector, Integer> {

    List<Sector> findAll();
    
}
