package org.springframework.samples.petclinic.card;

import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.player.Player;

public interface CardActions {

    void action(Player player, Hex origin, Hex target, Card card);
    
}
