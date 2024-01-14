package org.springframework.samples.petclinic.player;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "players")
public class Player extends Person {

    Boolean startPlayer;

    @Min(0)
    Integer score;

    PlayerRol rol;

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST })
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "players_friends", joinColumns = @JoinColumn(name = "player_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "player_id", "friend_id" }))
    List<Player> friends;
    
    @OneToMany(mappedBy = "player")
    @Size(min = 0, max = 3)
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Card> cards;

    @OneToMany(mappedBy = "player")
    @Size(min = 0, max = 15)
    @JsonIgnore
    private List<Ship> ships;

}
