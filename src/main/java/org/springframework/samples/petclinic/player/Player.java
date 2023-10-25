package org.springframework.samples.petclinic.player;

import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="player")
public class Player extends BaseEntity {
    
    @NotNull
    private String username;

    @NotNull
    private String mail;

    @Column(name="start_player")
    @NotNull
    private Boolean startPlayer;

    @NotNull
    private Integer score;

}
