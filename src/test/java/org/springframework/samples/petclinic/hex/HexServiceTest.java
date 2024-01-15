package org.springframework.samples.petclinic.hex;

import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class HexServiceTest {

    HexService hexService;

    @Autowired
    public HexServiceTest(HexService hexService) {
        this.hexService = hexService;
    }
    
    @Test
    void shouldReturnTrueIfHexsAreNeighbours() {

        Hex hex1 = new Hex();
        hex1.setPuntos(0);
        hex1.setPosition(0);
        hex1.setOccuped(false);

        Hex hex2 = new Hex();
        hex2.setPuntos(0);
        hex2.setPosition(1);
        hex2.setOccuped(false);

        assertTrue(hexService.isNeighbour(hex1, hex2));
    }

    @Test
    void shouldReturnFalseIfHexsAreNotNeighbours() {

        Hex hex1 = new Hex();
        hex1.setPuntos(0);
        hex1.setPosition(0);
        hex1.setOccuped(false);

        Hex hex2 = new Hex();
        hex2.setPuntos(0);
        hex2.setPosition(20);
        hex2.setOccuped(false);

        assertFalse(hexService.isNeighbour(hex1, hex2));
    }

    @Test
    void shouldReturnTrueIfHexIsNeighbourOfAnotherHex() {

        Hex hex1 = new Hex();
        hex1.setPuntos(0);
        hex1.setPosition(0);
        hex1.setOccuped(false);

        Hex hex2 = new Hex();
        hex2.setPuntos(0);
        hex2.setPosition(1);
        hex2.setOccuped(false);

        Hex hex3 = new Hex();
        hex3.setPuntos(0);
        hex3.setPosition(4);
        hex3.setOccuped(false);

        assertTrue(hexService.isNeighbourOfMyNeighbours(hex1, hex3));

    }

    @Test
    void shouldReturnFalseIfHexIsNotNeighbourOfAnotherHex() {

        Hex hex1 = new Hex();
        hex1.setPuntos(0);
        hex1.setPosition(8);
        hex1.setOccuped(false);
        hexService.save(hex1);

        Hex hex2 = new Hex();
        hex2.setPuntos(0);
        hex2.setPosition(7);
        hex2.setOccuped(false);
        hexService.save(hex2);

        Hex hex3 = new Hex();
        hex3.setPuntos(0);
        hex3.setPosition(1);
        hex3.setOccuped(false);
        hexService.save(hex3);

        assertFalse(hexService.isNeighbourOfMyNeighbours(hex1, hex3));

    }

}
