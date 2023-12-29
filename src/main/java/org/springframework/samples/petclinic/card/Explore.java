package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.NotOwnedHex;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.transaction.annotation.Transactional;

public class Explore implements CardActions {

    @Autowired
    HexService hexService;

    @Autowired
    ShipService shipService;

    @Override
    @Transactional
    //Desplazar tres flotas (o todas las que tenga si es menor de 3)
    //Cambiar que no sean todas y que tú puedas decidir cuantas mover
    public void action(Player player, Hex origin, Hex target) throws NotOwnedHex{
        if(player.equals(hexService.findPlayerInHex(origin.getId()))) {
            if((player.equals(hexService.findPlayerInHex(target.getId())) || target.getOccuped() == false)
            && (hexService.isNeighbour(origin, target) || hexService.isNeighbourOfMyNeighbours(origin, target))) {
                List<Ship> shipsInOrigin = hexService.findShipsInHex(origin.getId());
                for(int i = 0; i < shipsInOrigin.size(); i++) { 
                    Ship ship = shipsInOrigin.get(i); 
                    ship.setHex(target);
                    shipService.updateShip(ship, ship.getId());
                }
                origin.setOccuped(false);
                hexService.updateHex(origin, origin.getId());
                target.setOccuped(true);
                hexService.updateHex(target, target.getId());
            }else{
                throw new NotOwnedHex("No puedes moverte a esa posición");
            }
        }else{
            throw new NotOwnedHex("Este sistema no te pertenece");
        }
    }
    
}