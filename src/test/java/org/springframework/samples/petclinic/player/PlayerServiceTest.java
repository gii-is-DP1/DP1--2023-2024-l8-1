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
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.user.Authorities;
import org.springframework.samples.petclinic.user.AuthoritiesService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.security.test.context.support.WithMockUser;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class PlayerServiceTest {
	@Autowired
	PlayerService playerService;

	@Autowired
	UserService userService;

	@Autowired
	AuthoritiesService authoritiesService;

	User player2User;

	@BeforeEach
	public void setup() {

		Authorities playerAuth = new Authorities();
		// playerAuth.setId(1);
		playerAuth.setAuthority("PLAYER");
		authoritiesService.saveAuthorities(playerAuth);

		player2User = new User();
		// player2User.setId(25);
		player2User.setUsername("player2Test");
		player2User.setPassword("player2Test");
		player2User.setAuthority(playerAuth);

		userService.saveUser(player2User);

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

	/*
	 * @Test
	 * void shouldNotCreateAPlayerSucessfully() {
	 * 
	 * Player newPlayer = creatNewPlayerWithUsedUser();
	 * // playerService.savePlayer(newPlayer);
	 * // assertEquals(newPlayer.getId(),
	 * playerService.findPlayerByUser(24).getId());
	 * assertThrows(TransactionSystemException.class, () -> {
	 * playerService.savePlayer(newPlayer);
	 * });
	 * 
	 * }
	 */
	private Player creatNewPlayerWithUsedUser() {

		Player newPlayer = new Player();
		newPlayer.setFirstName("Daniel");
		newPlayer.setLastName("Alors");
		newPlayer.setUser(userService.findUser(24));

		return newPlayer;

	}

	@Test
	void shoudlCreatePlayerSucessfully() {

		List<Player> databasePlayers = playerService.findAll();
		playerService.savePlayer(createValidPlayer());
		List<Player> newDatabasePlayers = playerService.findAll();
		assertEquals(databasePlayers.size() + 1, newDatabasePlayers.size());

	}

	private Player createValidPlayer() {

		Player newPlayer = new Player();
		newPlayer.setFirstName("Daniel");
		newPlayer.setLastName("Alors");
		newPlayer.setUser(player2User);

		return newPlayer;

	}

	/*
	 * @Test
	 * void shouldNotCreateValidPlayer() {
	 * 
	 * Player newPlayer = new Player();
	 * newPlayer.setFirstName("");
	 * 
	 * assertThrows(TransactionSystemException.class, () -> {
	 * playerService.savePlayer(newPlayer);
	 * });
	 * }
	 */

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
	void shouldAddANewFriend() {

		Player me = playerService.findPlayerById(1);

		Player newFriend = createValidPlayer();
		newFriend.setFriends(new ArrayList<Player>());
		playerService.savePlayer(newFriend);
		playerService.addFriend(me, newFriend.getId());
		playerService.savePlayer(me);

		Player meUpdate = playerService.findPlayerById(1);
		List<Player> updatedUserPlayerFriends = meUpdate.getFriends();
		assertTrue(updatedUserPlayerFriends.contains(newFriend));

	}

	@Test
	void shouldNotAddFriendIfTheyAreAlreadyFriends() {

		Player me = playerService.findPlayerById(1);
		List<Player> userPlayerFriends = me.getFriends();

		playerService.addFriend(me, 6);

		List<Player> updatedUserPlayerFriends = me.getFriends();
		assertEquals(userPlayerFriends.size(), updatedUserPlayerFriends.size());

	}

	@Test
	void shouldDeletePlayer() {

		Player newPlayer = createValidPlayer();
		playerService.savePlayer(newPlayer);
		List<Player> databasePlayers = playerService.findAll();

		playerService.deletePlayer(newPlayer.getId());

		List<Player> updatedPlayers = playerService.findAll();

		assertEquals(databasePlayers.size() - 1, updatedPlayers.size());

	}

}
