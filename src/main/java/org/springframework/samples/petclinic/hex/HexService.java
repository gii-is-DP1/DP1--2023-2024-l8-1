package org.springframework.samples.petclinic.hex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.ship.Ship;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;

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

    @Transactional(readOnly=true)
    public Hex findHexById(int id) throws DataAccessException {
		return hr.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hex", "ID", id));
    }

    @Transactional
    public Hex updateHex(Hex hex, int id){
        Hex toUpdate = findHexById(id);
        BeanUtils.copyProperties(hex,toUpdate,"id");
        hr.save(toUpdate);

        return toUpdate;
    }

    @Transactional
    public Hex save(Hex hex) throws DataAccessException {
        return hr.save(hex);
    }

    public List<Hex> listAdyacencias(Hex hex, String name) {
        Integer posicion = hex.getPosition();
        List<Integer> posiciones = Adyacencias.adyacentesPorPosicion.get(posicion);
        List<Hex> vecinos = new ArrayList<>();
        for(int i=0; i<posiciones.size() ;i++) {
            Hex vecino = hr.findHexByPositionInGame(posiciones.get(i), name);
            vecinos.add(vecino);
        }
        return vecinos;
    }

    @Transactional(readOnly = true)
    public Player findPlayerInHex(Integer id) {
        Optional<Player> player = hr.findPlayerInHex(id);
        return player.isPresent() ? player.get() : null;
    }

    @Transactional(readOnly = true)
    public List<Ship> findShipsInHex(Integer id) {
        return hr.findShipsInHex(id);
    }

    @Transactional(readOnly = true)
    public Boolean isNeighbour(Hex hex1, Hex hex2) {
        List<Integer> neighbours = Adyacencias.adyacentesPorPosicion.get(hex1.getPosition());
        return neighbours.contains(hex2.getPosition());
    }

    @Transactional(readOnly = true) 
    public Boolean isNeighbourOfMyNeighbours(Hex hex1, Hex hex2) {
        List<Integer> neighbours = Adyacencias.adyacentesPorPosicion.get(hex1.getPosition());
        Boolean areAdjacents = false;
        int i = 0;
        while (areAdjacents == false || i < neighbours.size()) {
            // para cada elemento de neighbours que la posicion del hex2 este en en las posiciones del que cojo
            areAdjacents = Adyacencias.adyacentesPorPosicion.get(neighbours.get(i)).contains(hex2.getPosition());
            i++;
        }
        return areAdjacents;
    }


}
