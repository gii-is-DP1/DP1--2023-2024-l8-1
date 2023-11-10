package org.springframework.samples.petclinic.player;

import org.springframework.samples.petclinic.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Players")
public class Player extends User {

    @NotNull
    Boolean startPlayer;

    @NotEmpty
    @Min(0)
    Integer score;

    @NotEmpty
    PlayerRol rol;



}
