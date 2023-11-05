package org.springframework.samples.petclinic.game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.gameboard.GameBoard;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.round.Round;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name="games")
public class Game extends BaseEntity {

    @OneToOne(optional = false)
    private Player host;

    @Column(name="start_time")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH/mm")
    @NotNull
    private LocalDateTime startTime = LocalDateTime.now();

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

    @OneToOne
    @JoinColumn(name = "gameboard_ id")
    private GameBoard gameBoard;

    @OneToMany
    private List<Round> rounds;


}
