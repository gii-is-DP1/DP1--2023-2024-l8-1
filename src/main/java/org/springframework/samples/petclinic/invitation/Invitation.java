package org.springframework.samples.petclinic.invitation;

import java.util.List;

import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Invitation extends BaseEntity{

    @NotNull
    Boolean isAccepted;
    
    @NotEmpty
    InvitationType discriminator;

    @ManyToOne
    Player player;

    @OneToOne
    Game game;

}
