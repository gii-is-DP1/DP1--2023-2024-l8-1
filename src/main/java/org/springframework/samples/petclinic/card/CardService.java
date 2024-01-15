package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.val;

import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.Game;

@Service
public class CardService {

    CardRepository cardRepository;
    ShipService shipService;
    HexService hexService;
    PlayerService playerService;
    UserService userService;
    

    @Autowired
    public CardService(CardRepository cardRepository, ShipService shipService, HexService hexService,
            PlayerService playerService, UserService userService) {
        this.cardRepository = cardRepository;
        this.shipService = shipService;
        this.hexService = hexService;
        this.playerService = playerService;
        this.userService = userService;
    }

    @Transactional
    public Card saveCard(@Valid Card newCard) {
        return cardRepository.save(newCard);
    }

    @Transactional(readOnly = true)
    public Card findCardById(int id) throws DataAccessException {
        return cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card", "ID", id));
    }

    @Transactional
    public Card updateCard(Card card, int id) {
        Card toUpdate = findCardById(id);
        BeanUtils.copyProperties(card, toUpdate, "id");
        return saveCard(toUpdate);
    }

    @Transactional
    public void genCardsForOnePlayer(Integer playerId) {
        genExpandCard(playerId);
        genExploreCard(playerId);
        genExterminateCard(playerId);
    }

    @Transactional
    private void genExpandCard(Integer playerId) {
        Player player = playerService.findPlayerById(playerId);
        Card newCard = new Card();
        newCard.setType(CardType.EXPAND);
        newCard.setPlayer(player);
        newCard.setPerformingOrder(0);
        saveCard(newCard);

    }

    @Transactional
    private void genExploreCard(Integer playerId) {
        Player player = playerService.findPlayerById(playerId);
        Card newCard = new Card();
        newCard.setType(CardType.EXPLORE);
        newCard.setPlayer(player);
        newCard.setPerformingOrder(1);
        saveCard(newCard);
    }

    @Transactional
    private void genExterminateCard(Integer playerId) {
        Player player = playerService.findPlayerById(playerId);
        Card newCard = new Card();
        newCard.setType(CardType.EXTERMINATE);
        newCard.setPlayer(player);
        newCard.setPerformingOrder(2);
        saveCard(newCard);
    }

    @Transactional(readOnly = true)
    public List<Card> getPlayerCards(Integer playerId) {
        Player player = playerService.findPlayerById(playerId);
        return cardRepository.getCardsForPlayer(player.getId());
    }
    
    @Transactional
    public void setOrder(CardType type, Player player, Integer order, Game game) {
        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Turn turn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get();
        if (turn.getPlayer() == player){
            Card oldCard = cardRepository.findCardByOrder(order - 1, player.getId());
            Card card = cardRepository.findCardByType(type, player.getId());
            oldCard.setPerformingOrder(card.getPerformingOrder());
            card.setPerformingOrder(order - 1);

            updateCard(card, card.getId());
            updateCard(oldCard, oldCard.getId());
        } else {
            throw new AccessDeniedException("Ya no puedes ordenar tus cartas.");
        }
        
    }

}
