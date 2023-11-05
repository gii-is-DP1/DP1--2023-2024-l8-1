package org.springframework.samples.petclinic.phase;

import java.util.List;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.turn.Turn;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "phases")
public class Phase extends BaseEntity{
    
    private Boolean isOver;

    @OneToMany
    private List<Turn> turns;

}
