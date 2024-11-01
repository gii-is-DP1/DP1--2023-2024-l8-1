package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.game.GameService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "The Cards managemet API")
@SecurityRequirement(name = "bearerAuth")
public class CardRestController {

    private UserService userService;
    private CardService cardService;
    private GameService gameService;

    @Autowired
    public CardRestController(UserService userService, CardService cardService, GameService gameService) {
        this.userService = userService;
        this.cardService = cardService;
        this.gameService = gameService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Card>> findPlayerCards() {
        Player loggedPlayer = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Card> playerCards = cardService.getPlayerCards(loggedPlayer.getId());
        return new ResponseEntity<List<Card>>(playerCards, HttpStatus.OK);
    }

    @PutMapping("/{name}/{type}/{order}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> changePerformingOrder(@PathVariable("name") String name, @PathVariable("type") String cardType,
            @PathVariable("order") int performingOrder) {
        Player loggedPlayer = userService.findPlayerByUser(userService.findCurrentUser().getId());
        Game game = gameService.findByName(name);
        CardType cardTypeEnum;
        switch (cardType) {
            case "expand":
                cardTypeEnum = CardType.EXPAND;
                break;
            case "explore":
                cardTypeEnum = CardType.EXPLORE;
                break;
            case "exterminate":
                cardTypeEnum = CardType.EXTERMINATE;
                break;
            default:
                // Manejar un caso por defecto o lanzar una excepción si es necesario
                throw new IllegalArgumentException("Tipo de carta no válido: " + cardType);
        }

        cardService.setOrder(cardTypeEnum, loggedPlayer, performingOrder, game);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
