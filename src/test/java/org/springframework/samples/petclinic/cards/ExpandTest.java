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
import org.springframework.samples.petclinic.card.Expand;
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
class ExpandTest {

    //private Expand expand;
    private PlayerService playerService;
    private ShipService shipService;
    private HexService hexService;
    private CardService cardService;

    @Autowired
    public ExpandTest(PlayerService playerService, ShipService shipService, HexService hexService, CardService cardService){
        //this.expand = expand;
        this.playerService = playerService;
        this.shipService = shipService;
        this.hexService = hexService;
        this.cardService = cardService;
    }

    @Test
    @Transactional
    void shouldActionExpand(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);
        //Creo un hexagono
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(false);
        target.setPosition(0);
        hexService.save(target);

        //Asignar hexagono a nave
        ship1.setHex(target);
        shipService.updateShip(ship1, ship1.getId());

        //Asignar lista de naves a hexagonos
        List<Ship> lsShips = new ArrayList<>();
        lsShips.add(ship1);
        target.setShips(lsShips);
        hexService.updateHex(target, target.getId());
        int size = target.getShips().size();

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);
        shipService.save(ship2);

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPAND);
        card1.setUsesLeft(1);

        //Usar expand
        Expand Nexpand = new Expand(shipService, hexService, cardService);
        Nexpand.action(player1, target, target, card1);

        //Nos aseguramos de que se ha añadido la nave al hexagono
        assertEquals(size +1, target.getShips().size());
    }

    @Test
    @Transactional
    void YouCantPLAY(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        //Creo un hexagono
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(false);
        target.setPosition(0);
        hexService.save(target);

        //Creo la nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);
        shipService.save(ship2);

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPAND);
        card1.setUsesLeft(1);

        Expand Nexpand = new Expand(shipService, hexService, cardService);
        //Al no poseer ninguna nave en el tablero de juego no puede usar la carta EXPAND
        org.junit.jupiter.api.Assertions.assertThrows(YouCannotPlay.class, () -> Nexpand.action(player1, target, target, card1));
    }

    @Test
    @Transactional
    void justInSystems(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);
        //Creo un hexagono
        Hex target = new Hex();
        target.setPuntos(0);
        target.setOccuped(false);
        target.setPosition(0);
        hexService.save(target);

        //Asignar hexagono a nave
        ship1.setHex(target);
        shipService.updateShip(ship1, ship1.getId());

        //Asignar lista de naves a hexagonos
        List<Ship> lsShips = new ArrayList<>();
        lsShips.add(ship1);
        target.setShips(lsShips);
        hexService.updateHex(target, target.getId());

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);
        shipService.save(ship2);

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPAND);
        card1.setUsesLeft(1);

        Expand Nexpand = new Expand(shipService, hexService, cardService);

        //Nos aseguramos de que se ha añadido la nave al hexagono
        org.junit.jupiter.api.Assertions.assertThrows(NotOwnedHex.class, () -> Nexpand.action(player1, target, target, card1));
    }

    @Test
    @Transactional
    void NoHexes(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player2 = this.playerService.findPlayerById(2);
        //Creo una nave
        Ship ship3 = new Ship();
        ship3.setPlayer(player1);
        ship3.setState(ShipState.ON_GAME);
        shipService.save(ship3);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player2);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);
        //Creo un hexagono
        Hex target = new Hex();
        target.setPuntos(1);
        target.setOccuped(true);
        target.setPosition(0);
        hexService.save(target);

        //Asignar hexagono a nave
        ship1.setHex(target);
        shipService.updateShip(ship1, ship1.getId());

        //Asignar lista de naves a hexagonos
        List<Ship> lsShips = new ArrayList<>();
        lsShips.add(ship1);
        target.setShips(lsShips);
        hexService.updateHex(target, target.getId());

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);
        shipService.save(ship2);

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPAND);
        card1.setUsesLeft(1);

        //Usar expand
        Expand Nexpand = new Expand(shipService, hexService, cardService);

        //Nos aseguramos de que se ha añadido la nave al hexagono
        org.junit.jupiter.api.Assertions.assertThrows(NotOwnedHex.class, () -> Nexpand.action(player1, target, target, card1));

    }
}
