package org.springframework.samples.petclinic.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.card.Exterminate;
import org.springframework.samples.petclinic.exceptions.NotOwnedHex;
import org.springframework.samples.petclinic.exceptions.YouCannotPlay;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.ship.ShipState;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
class ExterminateTest {

    private PlayerService playerService;
    private ShipService shipService;
    private HexService hexService;
    private CardService cardService;

    @Autowired
    public ExterminateTest(PlayerService playerService, ShipService shipService, HexService hexService, CardService cardService){
        this.playerService = playerService;
        this.shipService = shipService;
        this.hexService = hexService;
        this.cardService = cardService;
    }

    @Test
    @Transactional
    void shouldWinActionExterminate(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player3 = this.playerService.findPlayerById(3);
        //Creo una nave para player1
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo otra nave para player1
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.ON_GAME);
        shipService.save(ship2);

        //Creo una nave para player3
        Ship ship3= new Ship();
        ship3.setPlayer(player3);
        ship3.setState(ShipState.ON_GAME);
        shipService.save(ship3);
        
        //Creo un hexagono origin
        Hex origin = new Hex();
        origin.setPuntos(1);
        origin.setOccuped(true);
        origin.setPosition(0);
        hexService.save(origin);
        
        //Creo un hexagono target
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(true);
        target.setPosition(1);
        hexService.save(target);

        //Asignar hexagonos a las nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        ship2.setHex(origin);
        shipService.updateShip(ship2, ship2.getId());
        ship3.setHex(target);
        shipService.updateShip(ship3, ship3.getId());

        //Asignar lista de naves a hexagono origin y sus adyacencias
        List<Ship> lsShips1 = new ArrayList<>();
        lsShips1.add(ship1); lsShips1.add(ship2);
        List<Hex> lsHex1 = new ArrayList<>();
        lsHex1.add(target);
        origin.setShips(lsShips1);
        origin.setAdyacentes(lsHex1);
        hexService.updateHex(origin, origin.getId());

        //Asignar lista de naves a hexagono target y sus adyacencias
        List<Ship> lsShips2 = new ArrayList<>();
        lsShips2.add(ship3);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXTERMINATE);
        card1.setUsesLeft(1);

        //Usar exterminar
        Exterminate Nexterminate = new Exterminate(hexService, shipService, cardService);
        Nexterminate.action(player1, origin, target, card1);

        //Nos aseguramos de que se ha añadido las nave perdidas pasan al State REMOVED y que las naves
        //Supervivientes pasan al hexagono correspondiente
        assertEquals(ShipState.REMOVED, ship1.getState());
        assertEquals(target.getPosition(), ship2.getHex().getPosition());
        assertEquals(ShipState.REMOVED, ship3.getState());
        assertEquals(origin.getOccuped(), false);
        assertEquals(target.getOccuped(), true);
    }

    @Test
    @Transactional
    void shouldDrawActionExterminate(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player3 = this.playerService.findPlayerById(3);
        //Creo una nave para player1
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo una nave para player3
        Ship ship3= new Ship();
        ship3.setPlayer(player3);
        ship3.setState(ShipState.ON_GAME);
        shipService.save(ship3);
        
        //Creo un hexagono origin
        Hex origin = new Hex();
        origin.setPuntos(1);
        origin.setOccuped(true);
        origin.setPosition(0);
        hexService.save(origin);
        
        //Creo un hexagono target
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(true);
        target.setPosition(1);
        hexService.save(target);

        //Asignar hexagonos a las nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        ship3.setHex(target);
        shipService.updateShip(ship3, ship3.getId());

        //Asignar lista de naves a hexagono origin y sus adyacencias
        List<Ship> lsShips1 = new ArrayList<>();
        lsShips1.add(ship1);
        List<Hex> lsHex1 = new ArrayList<>();
        lsHex1.add(target);
        origin.setShips(lsShips1);
        origin.setAdyacentes(lsHex1);
        hexService.updateHex(origin, origin.getId());

        //Asignar lista de naves a hexagono target y sus adyacencias
        List<Ship> lsShips2 = new ArrayList<>();
        lsShips2.add(ship3);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXTERMINATE);
        card1.setUsesLeft(1);

        //Usar exterminar
        Exterminate Nexterminate = new Exterminate(hexService, shipService, cardService);
        Nexterminate.action(player1, origin, target, card1);

        //Nos aseguramos de que se ha añadido las nave perdidas pasan al State REMOVED y que las naves
        //Supervivientes pasan al hexagono correspondiente
        assertEquals(ShipState.REMOVED, ship1.getState());
        assertEquals(ShipState.REMOVED, ship3.getState());
        assertEquals(origin.getOccuped(), false);
        assertEquals(target.getOccuped(), false);
    }

    @Test
    @Transactional
    void shouldLoseActionExterminate(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player3 = this.playerService.findPlayerById(3);
        //Creo una nave para player1
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo otra nave para player1
        Ship ship2 = new Ship();
        ship2.setPlayer(player3);
        ship2.setState(ShipState.ON_GAME);
        shipService.save(ship2);

        //Creo una nave para player3
        Ship ship3= new Ship();
        ship3.setPlayer(player3);
        ship3.setState(ShipState.ON_GAME);
        shipService.save(ship3);
        
        //Creo un hexagono origin
        Hex origin = new Hex();
        origin.setPuntos(1);
        origin.setOccuped(true);
        origin.setPosition(0);
        hexService.save(origin);
        
        //Creo un hexagono target
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(true);
        target.setPosition(1);
        hexService.save(target);

        //Asignar hexagonos a las nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        ship2.setHex(target);
        shipService.updateShip(ship2, ship2.getId());
        ship3.setHex(target);
        shipService.updateShip(ship3, ship3.getId());

        //Asignar lista de naves a hexagono origin y sus adyacencias
        List<Ship> lsShips1 = new ArrayList<>();
        lsShips1.add(ship1);
        List<Hex> lsHex1 = new ArrayList<>();
        lsHex1.add(target);
        origin.setShips(lsShips1);
        origin.setAdyacentes(lsHex1);
        hexService.updateHex(origin, origin.getId());

        //Asignar lista de naves a hexagono target y sus adyacencias
        List<Ship> lsShips2 = new ArrayList<>();
        lsShips2.add(ship2);lsShips2.add(ship3);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXTERMINATE);
        card1.setUsesLeft(1);

        //Usar exterminar
        Exterminate Nexterminate = new Exterminate(hexService, shipService, cardService);
        Nexterminate.action(player1, origin, target, card1);

        //Nos aseguramos de que se ha añadido las nave perdidas pasan al State REMOVED y que las naves
        //Supervivientes pasan al hexagono correspondiente
        assertEquals(ShipState.REMOVED, ship1.getState());
        assertEquals(target.getPosition(), ship3.getHex().getPosition());
        assertEquals(ShipState.REMOVED, ship2.getState());
        assertEquals(origin.getOccuped(), false);
        assertEquals(target.getOccuped(), true);
        assertEquals(ship3.getPlayer(), player3);
    }

    @Test
    @Transactional
    void YouCannotPlay(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player3 = this.playerService.findPlayerById(3);

        //Creo una nave para player3
        Ship ship3= new Ship();
        ship3.setPlayer(player3);
        ship3.setState(ShipState.ON_GAME);
        shipService.save(ship3);
        
        //Creo un hexagono origin
        Hex origin = new Hex();
        origin.setPuntos(1);
        origin.setOccuped(true);
        origin.setPosition(0);
        hexService.save(origin);
        
        //Creo un hexagono target
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(true);
        target.setPosition(1);
        hexService.save(target);

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXTERMINATE);
        card1.setUsesLeft(1);

        Exterminate Nexterminate = new Exterminate(hexService, shipService, cardService);

        org.junit.jupiter.api.Assertions.assertThrows(YouCannotPlay.class, () -> Nexterminate.action(player1, origin, target, card1));
    }

    @Test
    @Transactional
    void thisSystemIsntYours(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player3 = this.playerService.findPlayerById(3);
        //Creo una nave para player1
        Ship ship1 = new Ship();
        ship1.setPlayer(player3);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo una nave para player3
        Ship ship3= new Ship();
        ship3.setPlayer(player1);
        ship3.setState(ShipState.ON_GAME);
        shipService.save(ship3);
        
        //Creo un hexagono origin
        Hex origin = new Hex();
        origin.setPuntos(1);
        origin.setOccuped(true);
        origin.setPosition(0);
        hexService.save(origin);
        
        //Creo un hexagono target
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(true);
        target.setPosition(1);
        hexService.save(target);

        //Asignar hexagonos a las nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        ship3.setHex(target);
        shipService.updateShip(ship3, ship3.getId());

        //Asignar lista de naves a hexagono origin y sus adyacencias
        List<Ship> lsShips1 = new ArrayList<>();
        lsShips1.add(ship1);
        List<Hex> lsHex1 = new ArrayList<>();
        lsHex1.add(target);
        origin.setShips(lsShips1);
        origin.setAdyacentes(lsHex1);
        hexService.updateHex(origin, origin.getId());

        //Asignar lista de naves a hexagono target y sus adyacencias
        List<Ship> lsShips2 = new ArrayList<>();
        lsShips2.add(ship3);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXTERMINATE);
        card1.setUsesLeft(1);

        //Usar exterminar
        Exterminate Nexterminate = new Exterminate(hexService, shipService, cardService);
        
        org.junit.jupiter.api.Assertions.assertThrows(NotOwnedHex.class, () -> Nexterminate.action(player1, origin, target, card1));
    }
}
