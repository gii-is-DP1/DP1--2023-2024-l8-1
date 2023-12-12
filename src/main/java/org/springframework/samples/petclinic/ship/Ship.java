package org.springframework.samples.petclinic.ship;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ships")
public class Ship extends BaseEntity{

    @NotEmpty
    ShipState state;
    
}
