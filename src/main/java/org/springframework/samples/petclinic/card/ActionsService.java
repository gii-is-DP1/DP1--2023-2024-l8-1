package org.springframework.samples.petclinic.card;

import java.util.ArrayList;
import java.util.List;

import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.gameboard.GameBoard;
import org.springframework.samples.petclinic.gameboard.GameBoardService;
import org.springframework.samples.petclinic.hex.Hex;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.round.RoundService;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActionsService {

    UserService userService;
    ShipService shipService;
    HexService hexService;
    GameService gameService;
    CardService cardService;
    RoundService roundService;

    @Autowired
    public ActionsService(UserService userService, GameService gameService, ShipService shipService,
            HexService hexService, CardService cardService, RoundService roundService) {
        this.userService = userService;
        this.gameService = gameService;
        this.shipService = shipService;
        this.hexService = hexService;
        this.cardService = cardService;
        this.roundService = roundService;
    }

    /*@Transactional(readOnly = true)
    public List<Hex> getGameBoardHexs(GameBoard gameBoard) {
        List<Hex> aux = new ArrayList<>();
        for (Sector s : gameBoard.getSectors()) {
            for (Hex h : s.getHexs()) {
                aux.add(h);
            }
        }
        return aux;
    }

    @Transactional
    public void useExpandCard(Game game, Integer position, Player player) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver() && s.getIsPoint()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        if (turn.getPlayer() == player) {
            Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
            List<Hex> hexs = getGameBoardHexs(game.getGameBoard());
            Hex target = hexs.get(position);
            if (target.getOccuped()) {
                if (target.getShips().get(0).getPlayer() == player) {
                    Expand expand = new Expand(shipService, hexService);
                    expand.action(me, null, target);
                } else {
                    throw new AccessDeniedException("El hexagono debe ser tuyo.");
                }
            } else {
                throw new AccessDeniedException("El hexagono debe estar ocupado.");
            }
        } else {
            throw new AccessDeniedException("No es tu turno.");
        }
        Card card = gameService.getCurrentAction(game);
        card.setUsesLeft(card.getUsesLeft() - 1);
        cardService.saveCard(card);
        if (card.getUsesLeft() == 0) {
            turn.setIsOver(null);
            roundService.roundIsOver(round, phase, game);
        }

    }

    @Transactional
    public void useExploreCard(Game game, Integer positionOrigin, Integer positionTarget, Player player) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver() && s.getIsPoint()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        if (turn.getPlayer() == player) {
            List<Hex> hexs = getGameBoardHexs(game.getGameBoard());
            Hex origin = hexs.get(positionOrigin);
            if (origin.getOccuped()) {
                if (origin.getShips().get(0).getPlayer() == player) {
                    Hex target = hexs.get(positionTarget);
                    if (!target.getOccuped()){
                        Explore explore = new Explore(hexService, shipService);
                        explore.action(player, origin, target);
                    }
                } else {
                    throw new AccessDeniedException("El hexagono origen debe ser tuyo.");
                }
            } else {
                throw new AccessDeniedException("El hexagono origen debe estar ocupado.");
            }
        } else {
            throw new AccessDeniedException("No es tu turno.");
        }
        Card card = gameService.getCurrentAction(game);
        card.setUsesLeft(card.getUsesLeft() - 1);
        cardService.saveCard(card);
        if (card.getUsesLeft() == 0) {
            turn.setIsOver(null);

            roundService.roundIsOver(round, phase, game);
        }
    }

    @Transactional
    public void useExterminateCard(Game game, Integer positionOrigin, Integer positionTarget, Player player) {
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Hex> hexs = getGameBoardHexs(game.getGameBoard());
        Hex origin = hexs.get(positionOrigin);
        Hex target = hexs.get(positionTarget);
        Exterminate exterminate = new Exterminate(hexService, shipService);
        exterminate.action(me, origin, target);
    }*/

}
