package org.springframework.samples.petclinic.game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;


import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name="game")
public class Game extends BaseEntity{

    @NotNull
    private String admin;

    @Column(name="start_time")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH/mm")
    @NotNull
    private LocalDateTime startTime= LocalDateTime.now();

    @Column(name="end_time")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH/mm")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @NotNull
    GameState state;

    @OneToOne
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "player_id")
    private Player winner;


}
