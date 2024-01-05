package org.springframework.samples.petclinic.card;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.NotOwnedHex;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.transaction.annotation.Transactional;

public class Exterminate implements CardActions {

    @Autowired
    HexService hexService;

    @Autowired
    ShipService shipService;

    @Override
    @Transactional
    public void action(Player player, Hex origin, Hex target) {
        if (player.equals(hexService.findPlayerInHex(origin.getId()))) {
            if (hexService.isNeighbour(origin, target)) {
                List<Ship> myShips = hexService.findShipsInHex(origin.getId());
                List<Ship> enemyShips = hexService.findShipsInHex(target.getId());

                if (enemyShips != null) {
                    //GANAN LAS NAVES ALIADAS
                    if (myShips.size() > enemyShips.size()) {

                        for (int i = 0; i < enemyShips.size(); i++) {
                            Ship enemyShip = enemyShips.get(i);
                            Ship myShip = myShips.get(i);
                            enemyShip.setState(ShipState.REMOVED);
                            myShip.setState(ShipState.REMOVED);
                            shipService.updateShip(enemyShip, enemyShip.getId());
                            shipService.updateShip(myShip, myShip.getId());
                        }

                        List<Ship> remaining = new ArrayList<>();
                        for (int i = 0; i < myShips.size(); i++) {
                            if (myShips.get(i).getState() == ShipState.ON_GAME) {
                                remaining.add(myShips.get(i));
                            }
                            for (int j = 0; j < remaining.size(); j++) {
                                Ship shipInEnemyHex = remaining.get(j);
                                shipInEnemyHex.setHex(target);
                                shipService.updateShip(shipInEnemyHex, shipInEnemyHex.getId());
                            }
                        }
                        origin.setOccuped(false);
                        hexService.updateHex(origin, origin.getId());
                    //EMPATAN
                    } else if (myShips.size() == enemyShips.size()) {

                        for (int i = 0; i < enemyShips.size(); i++) {
                            Ship enemyShip = enemyShips.get(i);
                            Ship myShip = myShips.get(i);
                            enemyShip.setState(ShipState.REMOVED);
                            myShip.setState(ShipState.REMOVED);
                            shipService.updateShip(enemyShip, enemyShip.getId());
                            shipService.updateShip(myShip, myShip.getId());
                        }
                        target.setOccuped(false);
                        hexService.updateHex(target, target.getId());
                        origin.setOccuped(false);
                        hexService.updateHex(origin, origin.getId());

                    } else {
                            //GANAN LAS NAVES ENEMIGAS
                        for (int i = 0; i < myShips.size(); i++) {
                            Ship enemyShip = enemyShips.get(i);
                            Ship myShip = myShips.get(i);
                            enemyShip.setState(ShipState.REMOVED);
                            myShip.setState(ShipState.REMOVED);
                            shipService.updateShip(enemyShip, enemyShip.getId());
                            shipService.updateShip(myShip, myShip.getId());
                        }

                        List<Ship> remaining = new ArrayList<>();
                        for (int i = 0; i < enemyShips.size(); i++) {
                            if (enemyShips.get(i).getState() == ShipState.ON_GAME) {
                                remaining.add(enemyShips.get(i));
                            }
                        }
                        origin.setOccuped(false);
                        hexService.updateHex(origin, origin.getId());
                    }
                } else {
                    for (int i = 0; i < myShips.size(); i++) {
                        Ship shipInEnemyHex = myShips.get(i);
                        shipInEnemyHex.setHex(target);
                        shipService.updateShip(shipInEnemyHex, shipInEnemyHex.getId());
                    }
                    origin.setOccuped(false);
                    hexService.updateHex(origin, origin.getId());
                }

            } else {
                throw new NotOwnedHex("No puedes moverte a esa posición");
            }
        } else {
            throw new NotOwnedHex("Este sistema no te pertenece");
        }
    }

}
