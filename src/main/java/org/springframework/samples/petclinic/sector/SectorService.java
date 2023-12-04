package org.springframework.samples.petclinic.sector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.stereotype.Service;

@Service
public class SectorService {
    
    private SectorRepository sectorRepository;
    private HexService hexService;
    private Random random = new Random();

    @Autowired
    public SectorService(SectorRepository ssectorRepositoryr, HexService hexService){
        this.sectorRepository=sectorRepository;
        this.hexService=hexService;
    }
    

    public Sector genTriPrime(){
        Sector newSector = new Sector();
        List<Hex> aux = new ArrayList<>();
        newSector.setIsTriPrime(true);
        aux.add(hexService.genTriHex());
        newSector.setHexs(aux);
        return sectorRepository.save(newSector);
    } 

    public Sector genRandom() {
        Sector newSector = new Sector();
        List<Hex> aux = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            aux.add(hexService.genRandomHex());
        }
        newSector.setHexs(aux);
        return sectorRepository.save(newSector);
    }

}
