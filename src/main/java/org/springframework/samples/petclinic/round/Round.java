package org.springframework.samples.petclinic.round;

import java.util.List;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.phase.Phase;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rounds")
public class Round extends BaseEntity{

    private Boolean isOver;

    private Boolean isFinal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(min = 3, max = 3)
    private List<Phase> phases;
    
}
