package org.springframework.samples.petclinic.card;

import java.util.List;

import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.gameboard.GameBoardService;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionsService {

    UserService userService;
    GameBoardService gameBoardService;
    ShipService shipService;
    HexService hexService;

    @Autowired
    public ActionsService(UserService userService, GameBoardService gameBoardService, ShipService shipService, HexService hexService) {
        this.userService = userService;
        this.gameBoardService = gameBoardService;
        this.shipService = shipService;
        this.hexService = hexService;
    }

    @Transactional
    public void useExpandCard(Game game, Integer position) {
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Hex> hexs = gameBoardService.getGameBoardHexs(game.getGameBoard());
        Hex target = hexs.get(position);
        Expand expand = new Expand(shipService, hexService);
        expand.action(me, null, target);
    }

    @Transactional
    public void useExploreCard(Game game, Integer positionOrigin, Integer positionTarget) {
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Hex> hexs = gameBoardService.getGameBoardHexs(game.getGameBoard());
        Hex origin = hexs.get(positionOrigin);
        Hex target = hexs.get(positionTarget);
        Explore explore = new Explore(hexService, shipService);
        explore.action(me, origin, target);
    }

    @Transactional
    public void useExterminateCard(Game game, Integer positionOrigin, Integer positionTarget) {
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Hex> hexs = gameBoardService.getGameBoardHexs(game.getGameBoard());
        Hex origin = hexs.get(positionOrigin);
        Hex target = hexs.get(positionTarget);
        Exterminate exterminate = new Exterminate(hexService, shipService);
        exterminate.action(me, origin, target);
    }
    
}
