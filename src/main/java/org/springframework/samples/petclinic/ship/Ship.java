package org.springframework.samples.petclinic.ship;

import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
    private ShipState state;

    @ManyToOne
    private Hex hex;

    @ManyToOne
    private Player player;
    
}
