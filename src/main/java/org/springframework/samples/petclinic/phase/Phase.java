package org.springframework.samples.petclinic.phase;


import java.util.List;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.turn.Turn;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "phases")
public class Phase extends BaseEntity{
    
    private Boolean isOver = false;

    private Boolean isPoint = false;

    @OneToMany
    @Size(min = 3, max = 3)
    // @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Turn> turns;

}
