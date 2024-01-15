package org.springframework.samples.petclinic.ship;

import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ships")
public class Ship extends BaseEntity{

    @Enumerated(EnumType.STRING)
    @NotNull
    private ShipState state;

    @ManyToOne(optional = true)
    private Hex hex;

    @ManyToOne(optional = false)
    private Player player;
    
}
