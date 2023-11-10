package org.springframework.samples.petclinic.round;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Round extends BaseEntity{

    @NotNull
    Boolean isOver;

    @NotNull
    Boolean isFinal;
    
}
