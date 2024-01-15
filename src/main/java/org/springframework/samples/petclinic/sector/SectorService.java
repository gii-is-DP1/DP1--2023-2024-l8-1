package org.springframework.samples.petclinic.sector;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectorService {

    private SectorRepository sectorRepository;
    private HexService hexService;

    @Autowired
    public SectorService(SectorRepository sectorRepository, HexService hexService) {
        this.sectorRepository = sectorRepository;
        this.hexService = hexService;
    }

    @Transactional(readOnly = true)
    public List<Sector> findAll(){
        return sectorRepository.findAll();
    }

    @Transactional
    public Sector genTriPrime() {
        Sector newSector = new Sector();
        List<Hex> aux = new ArrayList<>();
        newSector.setIsTriPrime(true);
        newSector.setPosition(6);
        aux.add(hexService.genTriHex());
        newSector.setHexs(aux);
        return sectorRepository.save(newSector);
    }

    @Transactional
    public Sector genRandom() {
        Sector newSector = new Sector();
        List<Hex> aux = new ArrayList<>();
        Boolean standard = false;

        while (!standard) {
            Integer onePointers = 0;
            Integer twoPointers = 0;
            for (int i = 0; i < 7; i++) {
                Hex hex = hexService.genRandomHex();
                if (hex.getPuntos() == 1)
                    onePointers++;
                if (hex.getPuntos() == 2)
                    twoPointers++;
                aux.add(hex);
            }
            if (onePointers == 2 && twoPointers == 1) {
                hexService.saveList(aux);
                standard = true;
            } else {
                aux.clear();
            }
        }

        newSector.setHexs(aux);
        return sectorRepository.save(newSector);
    }

    @Transactional
    public Sector save(Sector sector){
        return sectorRepository.save(sector);
    }

}
