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
import org.springframework.samples.petclinic.card.Explore;
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
class ExploreTest {

    private PlayerService playerService;
    private ShipService shipService;
    private HexService hexService;
    private CardService cardService;

    @Autowired
    public ExploreTest(PlayerService playerService, ShipService shipService, HexService hexService, CardService cardService){
        this.playerService = playerService;
        this.shipService = shipService;
        this.hexService = hexService;
        this.cardService = cardService;
    }

    @Test
    @Transactional
    void shouldActionExplore(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.ON_GAME);
        shipService.save(ship2);
        
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

        //Asignar hexagono a nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        int nav1 = ship1.getHex().getPosition();
        ship2.setHex(target);
        shipService.updateShip(ship2, ship2.getId());

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
        lsShips2.add(ship2);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPLORE);
        card1.setUsesLeft(1);

        //Usar explore
        Explore NExplore = new Explore(hexService, shipService, cardService);
        NExplore.action(player1, origin, target, card1);

        //Nos aseguramos de que se ha aÃ±adido la nave al hexagono
        assertEquals(nav1+1, ship1.getHex().getPosition());
    }

    @Test
    @Transactional
    void YouCantPLAY(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.IN_SUPPLY);
        shipService.save(ship1);

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.IN_SUPPLY);
        shipService.save(ship2);
        
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
        card1.setType(CardType.EXPLORE);
        card1.setUsesLeft(1);

        //Usar expand
        Explore NExplore = new Explore(hexService, shipService, cardService);
        
        org.junit.jupiter.api.Assertions.assertThrows(YouCannotPlay.class, () -> NExplore.action(player1, origin, target, card1));

    }

    @Test
    @Transactional
    void youUnderestimateMyPower(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player2 = this.playerService.findPlayerById(2);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player2);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player1);
        ship2.setState(ShipState.ON_GAME);
        shipService.save(ship2);
        
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

        //Asignar hexagono a nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        ship2.setHex(target);
        shipService.updateShip(ship2, ship2.getId());

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
        lsShips2.add(ship2);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPLORE);
        card1.setUsesLeft(1);

        //Usar explore
        Explore NExplore = new Explore(hexService, shipService, cardService);

        //Vemos que al no poseer dicho hexagono no nos deja mover sus naves al nuestro
        org.junit.jupiter.api.Assertions.assertThrows(NotOwnedHex.class, () -> NExplore.action(player1, origin, target, card1));

    }

    @Test
    @Transactional
    void FriendOrFoe(){
        //Saco jugador de la base de datos
        Player player1 = this.playerService.findPlayerById(1);
        Player player2 = this.playerService.findPlayerById(2);
        //Creo una nave
        Ship ship1 = new Ship();
        ship1.setPlayer(player1);
        ship1.setState(ShipState.ON_GAME);
        shipService.save(ship1);

        //Creo otra nave
        Ship ship2 = new Ship();
        ship2.setPlayer(player2);
        ship2.setState(ShipState.ON_GAME);
        shipService.save(ship2);
        
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

        //Asignar hexagono a nave
        ship1.setHex(origin);
        shipService.updateShip(ship1, ship1.getId());
        ship2.setHex(target);
        shipService.updateShip(ship2, ship2.getId());

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
        lsShips2.add(ship2);
        List<Hex> lsHex2 = new ArrayList<>();
        lsHex2.add(origin);
        target.setShips(lsShips2);
        target.setAdyacentes(lsHex2);
        hexService.updateHex(target, target.getId());

        //Creo una carta
        Card card1 = new Card();
        card1.setType(CardType.EXPLORE);
        card1.setUsesLeft(1);

        Explore NExplore = new Explore(hexService, shipService, cardService);

        //Vemos que al movernos de un hexagono nuestro a otro rival nos salga la excepcion esperada
        org.junit.jupiter.api.Assertions.assertThrows(NotOwnedHex.class, () -> NExplore.action(player1, origin, target, card1));

    }
    
}