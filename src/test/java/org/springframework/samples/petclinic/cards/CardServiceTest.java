package org.springframework.samples.petclinic.cards;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.card.Card;
import org.springframework.samples.petclinic.card.CardRepository;
import org.springframework.samples.petclinic.card.CardService;
import org.springframework.samples.petclinic.card.CardType;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.game.GameState;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class CardServiceTest {

    private CardService cardService;
    private CardRepository cardRepository;
    private PlayerService playerService;
    private AuthoritiesService authoritiesService;
    private UserService userService;
    private GameService gameService;
    private PhaseService phaseService;

    @Autowired
    public CardServiceTest(CardService cardService, CardRepository cardRepository, PlayerService playerService, AuthoritiesService authoritiesService, UserService userService, GameService gameService, PhaseService phaseService) {
        this.cardService = cardService;
        this.cardRepository = cardRepository;
        this.playerService = playerService;
        this.authoritiesService = authoritiesService;
        this.userService = userService;
        this.gameService = gameService;
        this.phaseService = phaseService;
    }

    @Test
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
        assertEquals(0, found);
        assertEquals(3, newfound);
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
        cardService.saveCard(newCard);
        Integer oldPerformingOrder = newCard.getPerformingOrder();

        // Verificar que la carta exista antes de intentar actualizarla
        Card existingCard = this.cardService.findCardById(newCard.getId());
        assertNotNull(existingCard); // Asegurarse de que la carta existe

        // Actualizar la carta
        existingCard.setPerformingOrder(1);
        cardService.updateCard(existingCard, existingCard.getId());

        // Verificar que las cartas son diferentes después de la actualización
        assertNotEquals(oldPerformingOrder, existingCard.getPerformingOrder());
    }

    @Test
    public void notUpdateCards() {
        Card card = null;
        assertThrows(ResourceNotFoundException.class, () -> this.cardService.updateCard(card, 100));
    }

    private Player createValidPlayer() {

        Authorities playerAuth = new Authorities();
        playerAuth.setAuthority("PLAYER");
        authoritiesService.saveAuthorities(playerAuth);
        User player2User = new User();
        player2User.setUsername("player2Test");
        player2User.setPassword("player2Test");
        player2User.setAuthority(playerAuth);
        userService.saveUser(player2User);
        Player player = new Player();
        player.setFirstName("Player 2");
        player.setLastName("Player 2");
        return player;
    }

    private Game createValidGame() {
        Game newGame = new Game();
        List<Player> ls = new ArrayList<>();
        Player player = playerService.findPlayerById(1);
        newGame.setHost(player);
        newGame.setName("partidaTest");
        newGame.setPublica(true);
        newGame.setState(GameState.LOBBY);
        newGame.setStartTime(LocalDateTime.of(2023, 11, 11, 11, 11, 11));
        ls.add(playerService.findPlayerById(2));
        ls.add(playerService.findPlayerById(3));
        newGame.setPlayers(ls);
        return newGame;

    }

    @Test
    void shouldSetCardOrder() {

        Game game = createValidGame();
        gameService.saveGame(game);

        Player player = createValidPlayer();
        playerService.savePlayer(player);

        CardType expand = CardType.EXPAND;

        gameService.startGame(game.getName());
        game.getRounds().get(0).getPhases().get(0).setIsOver(true);
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(0));

        Round round = game.getRounds().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Phase phase = round.getPhases().stream().filter(s -> !s.getIsOver()).findFirst().get();
        Player playerTurn = phase.getTurns().stream().filter(s -> !s.getIsOver()).findFirst().get().getPlayer();

        cardService.setOrder(expand, playerTurn, 2, game);
        Card newCard = playerTurn.getCards().stream().filter(c->c.getType().equals(CardType.EXPAND)).findFirst().get();
        Integer newPerformingOrder = newCard.getPerformingOrder();

        assertTrue(newPerformingOrder == 1);

    }

    @Test
    void shouldNotSetCardOrder() {

        Game game = createValidGame();
        gameService.saveGame(game);

        Player player = createValidPlayer();
        playerService.savePlayer(player);

        CardType expand = CardType.EXPAND;

        gameService.startGame(game.getName());
        game.getRounds().get(0).getPhases().get(0).setIsOver(true);
        phaseService.savePhase(game.getRounds().get(0).getPhases().get(0));
        
        assertThrows(AccessDeniedException.class, () -> cardService.setOrder(expand, player, 2, game));

    }

}