package org.springframework.samples.petclinic.hex;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HexService {
    
    private HexRepository hr;
    private Random random = new Random();

    @Autowired
    public HexService(HexRepository hr){
        this.hr=hr;
    }

    @Transactional
    public Hex genTriHex(){
        Hex newHex = new Hex();
        newHex.setPuntos(3);
        newHex.setPosition(42);
        return hr.save(newHex);
    }

    @Transactional
    public Hex genRandomHex(){
        Hex newHex = new Hex();
        newHex.setPuntos(random.nextInt(3));
        return newHex;
    }

    @Transactional
    public void saveList(List<Hex> lista){
        for (Hex h : lista) { 
            hr.save(h);
        }
    }

    @Transactional
    public Hex save(Hex hex){
        return hr.save(hex);
    }

}
