package org.springframework.samples.petclinic.card;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.player.Player;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card extends BaseEntity{

    @NotNull
    @Enumerated(EnumType.STRING)
    CardType type;

    @Min(0)
    @Max(2)
    Integer performingOrder;

    @Min(0)
    @Max(3)
    Integer usesLeft;

    @ManyToOne(optional = false)
    Player player;
    
}
