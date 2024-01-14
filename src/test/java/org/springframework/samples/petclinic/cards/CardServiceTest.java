package org.springframework.samples.petclinic.cards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardRepository;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
class CardsServiceTest {

    private CardService cardService;
    private CardRepository cardRepository;
    private PlayerService playerService;

    @Autowired
    public CardsServiceTest(CardService cardService, CardRepository cardRepository, PlayerService playerService) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
        this.playerService = playerService;
    }

    @Test
    @Transactional
    public void shouldGenCards() {
        Iterable<Card> cards = cardRepository.findAll();
        int found = 0;

        for (Card card : cards) {
            found++;
        }

        cardService.genCardsForOnePlayer(1);
        Iterable<Card> newcards = cardRepository.findAll();
        int newfound = 0;

        for (Card card : newcards) {
            newfound++;
        }
        assertNotEquals(found, newfound);
    }

    @Test
    public void notGenCards() {
        assertThrows(ResourceNotFoundException.class, () -> this.cardService.genCardsForOnePlayer(100));
    }

    @Test
    public void shouldUpdateCard() {
        // Crear una nueva carta y guardarla en la base de datos
        Card newCard = new Card();
        newCard.setType(CardType.EXPAND);
        newCard.setPerformingOrder(3);
        newCard.setPlayer(playerService.findPlayerById(1));
        this.cardService.saveCard(newCard);

        // Verificar que la carta exista antes de intentar actualizarla
        Card existingCard = this.cardService.findCardById(newCard.getId());
        assertNotNull(existingCard); // Asegurarse de que la carta existe

        // Actualizar la carta
        existingCard.setPerformingOrder(1);
        cardService.updateCard(existingCard, existingCard.getId());

        // Verificar que las cartas son diferentes después de la actualización
        assertNotEquals(newCard, existingCard);
    }

    @Test
    public void notUpdateCards() {
        Card card = null;
        assertThrows(ResourceNotFoundException.class, () -> this.cardService.updateCard(card, 100));
    }

}