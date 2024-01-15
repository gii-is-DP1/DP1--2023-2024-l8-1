package org.springframework.samples.petclinic.sector;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class SectorServiceTests {

    private SectorService sectorService;

    @Autowired
    public SectorServiceTests(SectorService sectorService){
        this.sectorService = sectorService;
    }

    @Test
    public void shouldCreateCorrectTriPrime(){
        Sector s = this.sectorService.genTriPrime();
        int position = s.getPosition();
        int hexPoints = s.getHexs().get(0).getPuntos();
        assertEquals(true, s.getIsTriPrime());
        assertEquals(6, position);
        assertEquals(1 , s.getHexs().size());
        assertEquals(3, hexPoints);
    }

    @Test
    public void shouldCreateCorrectSector(){
        Sector s = this.sectorService.genRandom();
        int zeroPointers = 0;
        int onePointers = 0;
        int twoPointers = 0;
        for (Hex hex : s.getHexs()){
            if (hex.getPuntos()==0) zeroPointers++;
            if (hex.getPuntos()==1) onePointers++;
            if (hex.getPuntos()==2) twoPointers++;
        }
        assertEquals(4, zeroPointers);
        assertEquals(2, onePointers);
        assertEquals(1, twoPointers);
    }

    @Test
    public void shouldInsertSector(){
        int found = this.sectorService.findAll().size();
        Sector s = this.sectorService.genRandom();
        this.sectorService.save(s);
        int finalFound = this.sectorService.findAll().size();
        assertEquals(found+1, finalFound);
    }
    
}
