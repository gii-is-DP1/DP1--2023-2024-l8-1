package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
public class GameServiceTests {

    GameService gs;
    UserService us;
    PlayerService ps;

    @Autowired
    public GameServiceTests(GameService gs, UserService us,
            PlayerService ps) {
        this.gs = gs;
        this.us = us;
        this.ps = ps;
    }

    //Positive Tests
    
    //H7+E1 - Crear partida
    @Test
    @WithMockUser(username="player1",roles={"PLAYER"})
    @Transactional
    void shouldInsertGame() {
        int initialCount = this.gs.getGames().size();

        Game game = new Game();
        game.setName("Game de prueba");
        game.setStartTime(LocalDateTime.now());
        game.setState(GameState.LOBBY);

        this.gs.saveGame(game);
        Assertions.assertThat(game.getId().longValue()).isNotZero();

        int finalCount = this.gs.getGames().size();
        assertEquals(initialCount + 1, finalCount);

    }

    @Test
    @WithMockUser(username="player1",roles={"PLAYER"})
    @Transactional
    void shouldSeePlayersFromGame() {
        Collection<Player> players = new ArrayList<>();
        players.add(ps.findPlayerById(2));
        players.add(ps.findPlayerById(3));

        List<Player> gamePlayers = gs.findGamePlayers("prueba");

        assertEquals(players, gamePlayers);
        
    }

    // Negative Tests

    //H7-E1 - Crear partida sin estar registrado
    @Test
    void shouldNotInsertGameIfNotAuthenticated() {
        Game game = new Game();
        game.setName("Game de prueba");
        game.setStartTime(LocalDateTime.now());
        game.setState(GameState.LOBBY);

        assertThrows(ResourceNotFoundException.class, () -> this.gs.saveGame(game));
    }


}
