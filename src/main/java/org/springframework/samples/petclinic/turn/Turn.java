package org.springframework.samples.petclinic.turn;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Turn extends BaseEntity{
    
    @NotNull
    Boolean isOver;

}
