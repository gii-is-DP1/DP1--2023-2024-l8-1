/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.TransactionSystemException;

import jakarta.transaction.Transactional;
@SpringBootTest
@AutoConfigureTestDatabase
class PlayerServiceTest {
	@Autowired
	protected PlayerService playerService;

	@Autowired
	protected UserService userService;

	User player2User;

	@BeforeEach
    public void setup() {

        Authorities playerAuth = new Authorities();
        playerAuth.setId(1);
        playerAuth.setAuthority("PLAYER");

        player2User = new User();
        player2User.setId(2);
        player2User.setUsername("player2Test");
        player2User.setPassword("player2Test");
        player2User.setAuthority(playerAuth);

    }

	@Test
	void shouldFindPlayertWithCorrectId() {
		Player foundPlayer = this.playerService.findPlayerById(3);
		assertThat(foundPlayer.getUser().getUsername()).startsWith("player3");
	}

	@Test
	void shouldNotFindPlayerWithInorrectId() {
		assertThrows(ResourceNotFoundException.class, () -> this.playerService.findPlayerById(700));
	}

	@Test
	void shouldFindAllPlayers() {
		
		List<Player> players = playerService.findAll();

		Player player1 = EntityUtils.getById(players, Player.class, 1);
		assertEquals("Play", player1.getFirstName());
		assertEquals("Yer", player1.getLastName());
		Player player4 = EntityUtils.getById(players, Player.class, 4);
		assertEquals("Manuel", player4.getFirstName());
		assertEquals("Serrano", player4.getLastName());
	}

	@Test
	@WithMockUser(username = "player1", authorities = "PLAYER")
	void shouldFindPlayerFriends() {

		List<Player> playerFriends = playerService.getFriends();
		Player friend = EntityUtils.getById(playerFriends, Player.class, 6);
		assertEquals("Urbano", friend.getFirstName());
		assertEquals("Blanes", friend.getLastName());

	}

	@Test
	void shouldFindPlayerByUserId() {

		Player foundPlayer = playerService.findPlayerByUser(24);
		assertThat(foundPlayer.getUser().getUsername()).startsWith("player1");

	}

	@Test
	void shouldNotFindPlayerWithInorrectUserId() {
		assertNull(playerService.findPlayerByUser(300));
	}

	@Test
	void shouldFindPlayerRole() {
		assertEquals(PlayerRol.HOST, playerService.findPlayerRol(1));
	}

	@Test
	void shouldNotFindPlayerRoleForUnexistingPlayer() {
		assertNull(playerService.findPlayerRol(300));
	}

	//Aqui deberían ir las pruebas unitarias sobre encontrar el jugador inicial
	//Pero yo creo que esa funcion hay que redefinirla

	@Test
	@Transactional
	// Este Test debería fallar
	@WithMockUser(username = "player1", authorities = "PLAYER")
    void shouldNotCreateAPlayerSucessfully() {

        Player newPlayer= creatNewPlayerWithUsedUser();
        playerService.savePlayer(newPlayer);
       
    }

    private Player creatNewPlayerWithUsedUser() {

        Player newPlayer = new Player();
		newPlayer.setFirstName("Daniel");
		newPlayer.setLastName("Alors");
		newPlayer.setUser(userService.findCurrentUser());

		return newPlayer;

    }

	@Test
	@Transactional
	void shoudlCreatePlayerSucessfully() {

		List<Player> databasePlayers = playerService.findAll();
		playerService.savePlayer(createValidPlayer());
		assertEquals(databasePlayers.size() + 1, playerService.findAll().size());

	}

	private Player createValidPlayer() {

        Player newPlayer = new Player();
		newPlayer.setFirstName("Daniel");
		newPlayer.setLastName("Alors");
		newPlayer.setUser(player2User);

		return newPlayer;

    }

    @Test
    void shouldNotCreateValidPlayer() {

        assertThrows(TransactionSystemException.class, () -> {
            Player newPlayer = createAPlayerWithoutUser();
            playerService.savePlayer(newPlayer);
        });
    }

    private Player createAPlayerWithoutUser() {

        Player newPlayer = new Player();
		newPlayer.setFirstName("Daniel");
		newPlayer.setLastName("Alors");

		return newPlayer;

    }

    @Test
	@Transactional
    void shouldUpdatePlayer() {

        Player player = playerService.findPlayerById(6);
        player.setFirstName("Charlie");
        playerService.updatePlayer(player, player.getId());

        Player updatedPlayer = playerService.findPlayerById(6);
        assertEquals("Charlie", updatedPlayer.getFirstName());
		assertEquals("Blanes", updatedPlayer.getLastName());

    }

    @Test
    void shouldNotUpdateGame() {

        Player player = playerService.findPlayerById(5);
        player.setFirstName("");
        
        assertThrows(TransactionSystemException.class, () -> {
        playerService.updatePlayer(player, player.getId());});

    }

    @Test
	@Transactional
    @WithMockUser(username = "player3", authorities = "PLAYER")
    void shouldAddANewFriend() {

        Player userPlayer = playerService.findPlayerByUser(userService.findCurrentUser().getId());
		List<Player> userPlayerFriends = userPlayer.getFriends();
		
		playerService.addFriend(4);
		
		Player updatedUserPlayer = playerService.findPlayerByUser(userService.findCurrentUser().getId());
		List<Player> updatedUserPlayerFriends = updatedUserPlayer.getFriends();
		assertEquals(userPlayerFriends.size() + 1, updatedUserPlayerFriends.size());

    }

    @Test
    void shouldNotAddFriendIfNotAuthenticated() {

        assertThrows(ResourceNotFoundException.class, () -> {
        playerService.addFriend(4);;});

    }

	@Test
	@Transactional
	// Este Test debería fallar
	@WithMockUser(username = "player1", authorities = "PLAYER")
	void shouldNotAddFriendIfTheyAreAlreadyFriends() {

		Player userPlayer = playerService.findPlayerByUser(userService.findCurrentUser().getId());
		List<Player> userPlayerFriends = userPlayer.getFriends();
		
		playerService.addFriend(6);
		
		List<Player> updatedUserPlayerFriends = userPlayer.getFriends();
		assertEquals(userPlayerFriends.size(), updatedUserPlayerFriends.size());

	}

    @Test
	@Transactional
    void shouldDeletePlayer() {

        List<Player> databasePlayers = playerService.findAll();

        playerService.deletePlayer(4);

        List<Player> updatedPlayers = playerService.findAll();
        assertEquals(databasePlayers.size() - 1, updatedPlayers.size());

    }
	
}
