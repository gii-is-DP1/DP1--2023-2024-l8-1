package org.springframework.samples.petclinic.player;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.model.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="player")
public class Player extends Person {

    @NotNull
    Boolean startPlayer;

    @NotEmpty
    @Min(0)
    Integer score;

    @NotEmpty
    PlayerRol rol;



    @NotNull
    private Integer numCards = 3;

    @NotNull
    @Min(0)
    @Max(15)
    private Integer numShips;

}
