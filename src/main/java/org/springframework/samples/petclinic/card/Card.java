package org.springframework.samples.petclinic.card;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Card extends BaseEntity{

    @NotEmpty
    CardType type;

    @NotEmpty
    @Min(0)
    @Max(2)
    Integer performingOrder;

    @ManyToOne
    private Player player;

    
}
