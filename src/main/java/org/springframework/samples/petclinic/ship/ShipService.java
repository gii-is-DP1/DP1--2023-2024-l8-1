package org.springframework.samples.petclinic.ship;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;

@Service
public class ShipService {

    ShipRepository shipRepository;
    PlayerService playerService;

    @Autowired
    public ShipService(ShipRepository shipRepository, PlayerService playerService) {
        this.shipRepository = shipRepository;
        this.playerService = playerService;
    }

    @Transactional(readOnly = true)
    public Ship findShipById(int id) throws DataAccessException {
        return shipRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ship", "ID", id));
    }

    @Transactional
    public void genShipsForOnePlayer(Integer playerId) {
        Player player = playerService.findPlayerById(playerId);

        for (int i = 0; i < 15; i++) {
            Ship newShip = new Ship();
            newShip.setPlayer(player);
            newShip.setState(ShipState.IN_SUPPLY);
            shipRepository.save(newShip);
        }
    }

    @Transactional
    public List<Ship> selectShipsFromSupply(Integer id) {
        List<Ship> ls = shipRepository.findTopXShipsInSupplyState(id);
        return ls;
    }

    @Transactional
    public Ship save(Ship ship) throws DataAccessException {
        return shipRepository.save(ship);
    }

    @Transactional
    public Ship updateShip(Ship ship, int id) throws DataAccessException {
        Ship toUpdate = findShipById(id);
        BeanUtils.copyProperties(ship, toUpdate, "id");
        shipRepository.save(toUpdate);
        return toUpdate;
    }
}
