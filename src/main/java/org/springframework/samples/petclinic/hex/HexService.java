package org.springframework.samples.petclinic.hex;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HexService {
    
    private HexRepository hr;
    private Random random = new Random();

    @Autowired
    public HexService(HexRepository hr){
        this.hr=hr;
    }

    public Hex genTriHex(){
        Hex newHex = new Hex();
        newHex.setValue(3);
        return hr.save(newHex);
    }

    public Hex genRandomHex(){
        Hex newHex = new Hex();
        newHex.setValue(random.nextInt(3));
        return hr.save(newHex);
    }

}
