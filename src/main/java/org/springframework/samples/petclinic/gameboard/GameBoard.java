package org.springframework.samples.petclinic.gameboard;

import java.util.List;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.sector.Sector;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gameboards")
public class GameBoard extends BaseEntity {
    
    @OneToMany
    private List<Sector> sectors;

}
