package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.NotOwnedHex;
import org.springframework.samples.petclinic.exceptions.YouCannotPlay;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.transaction.annotation.Transactional;

public class Expand implements CardActions {

    ShipService shipService;
    HexService hexService;

    @Autowired
    public Expand(ShipService shipService, HexService hexService) {
        this.shipService = shipService;
        this.hexService = hexService;
    }

    @Override
    @Transactional
    // Esta accion solo debe sacar una nave del supply y asignarla a un hex
    // Decidir cuantas veces se repite y comprobar si tiene naves en el supply
    public void action(Player player, Hex origin, Hex target) throws NotOwnedHex, YouCannotPlay {
        
        // if (shipService.numOfShipsInGameForPlayer(player.getId()) > 0) {
            List<Ship> shipsInSupply = shipService.selectShipsFromSupply(player.getId());
            Ship shipToSet = shipsInSupply.get(0);
           // if (player.equals(hexService.findPlayerInHex(target.getId()))) {
                shipToSet.setState(ShipState.ON_GAME);
                shipToSet.setHex(target);
                shipService.updateShip(shipToSet, shipToSet.getId());
                target.setOccuped(true);
                hexService.updateHex(target, target.getId());
        //    } else {
        //        throw new NotOwnedHex("No tienes el control de este Hexagono");
        //    }
        //} else {
         //   throw new YouCannotPlay("No tienes suficientes naves sobre el tablero para jugar");
        //}
        
    }

}
