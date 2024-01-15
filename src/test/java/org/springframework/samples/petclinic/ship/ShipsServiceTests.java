package org.springframework.samples.petclinic.ship;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
class ShipsServiceTests {

    private ShipService shipService;
    private PlayerService playerService;

    @Autowired
    public ShipsServiceTests(ShipService shipService, PlayerService playerService){
        this.shipService = shipService;
        this.playerService = playerService;
    }

    @Test
    @Transactional
    void shouldGenShipsForOnePlayer(){
        //Cuento las naves que hay
        List<Ship> ships = shipService.findAllShips();
        int found = ships.size();

        //Genero las naves para un jugador
        shipService.genShipsForOnePlayer(1);
        //Vuelvo a guardar las naves para contarlas
        List<Ship> newships = shipService.findAllShips();
        assertNotEquals(found, newships.size());
    }

    @Test
    @Transactional
    void shouldNotGenShipsForOnePlayer(){
        //Veo que no se pueden generar naves para un jugador que no existe
        assertThrows(ResourceNotFoundException.class, () -> this.shipService.genShipsForOnePlayer(100));
    }

    @Test
    @Transactional
    void shouldSelectShipsFromSupply(){

        //Creo y guardo una nave
        Ship newShip = new Ship();
        newShip.setState(ShipState.IN_SUPPLY);
        newShip.setPlayer(playerService.findPlayerById(1));
        this.shipService.save(newShip);

        //Verificar que la nave tiene el estado IN_SUPPLY
        List<Ship> existingShip = this.shipService.selectShipsFromSupply(1);
        assertNotNull(existingShip); 
    }

    @Test
    @Transactional
    void shouldNotSelectShipsFromSupply(){

        //Creo y guardo una nave
        Ship newShip = new Ship();
        newShip.setState(ShipState.ON_GAME);
        newShip.setPlayer(playerService.findPlayerById(1));
        this.shipService.save(newShip);

        //Cuento las naves que hay
        List<Ship> ships = shipService.findAllShips();
        int found = ships.size();

        //Verificar que la nave que no tiene el estado IN_SUPPLY no se cuenta
        List<Ship> existingShip = this.shipService.selectShipsFromSupply(1);
        assertNotEquals(existingShip.size(), found); 
    }

    @Test
    @Transactional
    void shouldNumOfShipsInGameForPlayer(){

        //Creo y guardo una nave
        Ship newShip = new Ship();
        newShip.setState(ShipState.ON_GAME);
        newShip.setPlayer(playerService.findPlayerById(1));
        this.shipService.save(newShip);

        //Ver si el integer es distinto de 0
        Integer shipCount = this.shipService.numOfShipsInGameForPlayer(1);
        assertNotEquals(shipCount,0);
    }

    @Test
    @Transactional
    void shouldNotNumOfShipsInGameForPlayer(){

        //Ver si el integer es 0
        Integer shipCount = this.shipService.numOfShipsInGameForPlayer(1);
        assertEquals(shipCount,0);
    }

    @Test
    @Transactional
    void shouldUpdateCard() {
        // Crear una nueva nave y guardarla en la base de datos
        Ship newShip = new Ship();
        newShip.setState(ShipState.IN_SUPPLY);
        newShip.setPlayer(playerService.findPlayerById(1));
        this.shipService.save(newShip);

        // Verificar que la nave exista antes de intentar actualizarla
        Ship existingShip = this.shipService.findShipById(newShip.getId());
        assertNotNull(existingShip); // Asegurarse de que la nave existe

        // Actualizar la nave
        existingShip.setState(ShipState.REMOVED);
        shipService.updateShip(existingShip, existingShip.getId());

        // Verificar que la nave actualizada tiene el state REMOVED
        assertEquals(ShipState.REMOVED, existingShip.getState());
    }

    @Test
    @Transactional
    void ShouldNotUpdateShip() {
        Ship ship = null;
        assertThrows(ResourceNotFoundException.class, () -> this.shipService.updateShip(ship, 100));
    }
    
}
