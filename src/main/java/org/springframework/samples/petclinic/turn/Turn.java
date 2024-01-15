package org.springframework.samples.petclinic.turn;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "turns")
public class Turn extends BaseEntity{
    
    @NotNull
    Boolean isOver = false;

    @ManyToOne(optional=true)
    // @OnDelete(action = OnDeleteAction.CASCADE)
    private Player player;

}
