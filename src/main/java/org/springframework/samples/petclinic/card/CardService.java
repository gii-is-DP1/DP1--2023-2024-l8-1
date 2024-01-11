package org.springframework.samples.petclinic.card;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.hex.HexService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;

@Service
public class CardService {

    CardRepository cardRepository;
    ShipService shipService;
    HexService hexService;
    PlayerService playerService;

    @Autowired
    public CardService(CardRepository cardRepository, ShipService shipService, HexService hexService, PlayerService playerService) {
        this.cardRepository = cardRepository;
        this.shipService = shipService;
        this.hexService = hexService;
        this.playerService = playerService;
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
        newCard.setPerformingOrder(1);
        saveCard(newCard);
    }

}
