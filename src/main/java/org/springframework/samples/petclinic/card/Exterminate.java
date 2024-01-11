package org.springframework.samples.petclinic.card;

import java.util.ArrayList;
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

public class Exterminate implements CardActions {

    private HexService hexService;

    private ShipService shipService;

    @Autowired
    public Exterminate(HexService hexService, ShipService shipService) {
        this.hexService = hexService;
        this.shipService = shipService;
    }

    @Override
    @Transactional
    public void action(Player player, Hex origin, Hex target) {

        if (shipService.numOfShipsInGameForPlayer(player.getId()) > 0) {
            validateOwnership(player, origin);
            validateMovement(origin, target);

            List<Ship> myShips = hexService.findShipsInHex(origin.getId());
            List<Ship> enemyShips = hexService.findShipsInHex(target.getId());

            if (enemyShips != null) {
                battleOutcome(myShips, enemyShips, origin, target);
            } else {
                moveShipsToEmptyHex(myShips, target, origin);
            }
        } else {
            throw new YouCannotPlay("No tienes suficientes naves en juego sobre el tablero");
        }

    }

    private void validateOwnership(Player player, Hex hex) {
        if (!player.equals(hexService.findPlayerInHex(hex.getId()))) {
            throw new NotOwnedHex("Este sistema no te pertenece");
        }
    }

    private void validateMovement(Hex origin, Hex target) {
        if (!hexService.isNeighbour(origin, target)) {
            throw new NotOwnedHex("No puedes moverte a esa posición");
        }
    }

    private void battleOutcome(List<Ship> myShips, List<Ship> enemyShips, Hex origin, Hex target) {
        if (myShips.size() > enemyShips.size()) {
            winBattle(myShips, enemyShips, target);
        } else if (myShips.size() == enemyShips.size()) {
            drawBattle(myShips, enemyShips, origin, target);
        } else {
            loseBattle(myShips, enemyShips, origin);
        }
    }

    private void winBattle(List<Ship> myShips, List<Ship> enemyShips, Hex target) {
        removeShipsAndUpdateHex(myShips, enemyShips);
        moveRemainingShips(myShips, target);
        target.setOccuped(false);
        hexService.updateHex(target, target.getId());
    }

    private void drawBattle(List<Ship> myShips, List<Ship> enemyShips, Hex origin, Hex target) {
        removeShipsAndUpdateHex(myShips, enemyShips);
        origin.setOccuped(false);
        target.setOccuped(false);
        hexService.updateHex(origin, origin.getId());
        hexService.updateHex(target, target.getId());
    }

    private void loseBattle(List<Ship> myShips, List<Ship> enemyShips, Hex origin) {
        removeShipsAndUpdateHex(myShips, enemyShips);
        origin.setOccuped(false);
        hexService.updateHex(origin, origin.getId());
    }

    private void removeShipsAndUpdateHex(List<Ship> myShips, List<Ship> enemyShips) {
        if (myShips.size() >= enemyShips.size()) {
            for (int i = 0; i < enemyShips.size(); i++) {
                Ship enemyShip = enemyShips.get(i);
                Ship myShip = myShips.get(i);
                enemyShip.setState(ShipState.REMOVED);
                myShip.setState(ShipState.REMOVED);
                shipService.updateShip(enemyShip, enemyShip.getId());
                shipService.updateShip(myShip, myShip.getId());
            }
        } else {
            for (int i = 0; i < myShips.size(); i++) {
                Ship enemyShip = enemyShips.get(i);
                Ship myShip = myShips.get(i);
                enemyShip.setState(ShipState.REMOVED);
                myShip.setState(ShipState.REMOVED);
                shipService.updateShip(enemyShip, enemyShip.getId());
                shipService.updateShip(myShip, myShip.getId());
            }

        }
    }

    private void moveRemainingShips(List<Ship> myShips, Hex target) {
        List<Ship> remaining = new ArrayList<>();
        for (Ship ship : myShips) {
            if (ship.getState() == ShipState.ON_GAME) {
                remaining.add(ship);
            }
        }
        for (Ship ship : remaining) {
            ship.setHex(target);
            shipService.updateShip(ship, ship.getId());
        }
    }

    private void moveShipsToEmptyHex(List<Ship> myShips, Hex target, Hex origin) {
        for (Ship ship : myShips) {
            ship.setHex(target);
            shipService.updateShip(ship, ship.getId());
        }
        origin.setOccuped(false);
        hexService.updateHex(origin, origin.getId());
    }
} 