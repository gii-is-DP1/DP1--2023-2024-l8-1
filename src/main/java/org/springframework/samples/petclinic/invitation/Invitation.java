package org.springframework.samples.petclinic.invitation;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.samples.petclinic.game.Game;
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
@Table(name = "invitations")
public class Invitation extends BaseEntity{

    @NotNull
    Boolean isAccepted;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    InvitationType discriminator;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    Player playerTarget;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    Player playerSource;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    Game game;

}
