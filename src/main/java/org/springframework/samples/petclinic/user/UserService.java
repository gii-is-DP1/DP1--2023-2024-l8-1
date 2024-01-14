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
package org.springframework.samples.petclinic.user;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springdoc.api.ErrorMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.game.GameRepository;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerRepository;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserService {

	private UserRepository userRepository;

	// private OwnerService ownerService;
	//
	private VetService vetService;
	private PlayerRepository playerRepository;

	@Autowired
	public UserService(UserRepository userRepository, VetService vetService, PlayerRepository playerRepository) {
		this.userRepository = userRepository;
		// this.ownerService = ownerService;
		this.vetService = vetService;
		this.playerRepository = playerRepository;
	}

	@Transactional
	public User saveUser(User user) throws DataAccessException {
		userRepository.save(user);
		return user;
	}

	@Transactional(readOnly = true)
	public User findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	}

	@Transactional(readOnly = true)
	public User findUser(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

	@Transactional(readOnly = true)
	public Owner findOwnerByUser(String username) {
		return userRepository.findOwnerByUser(username)
				.orElseThrow(() -> new ResourceNotFoundException("Owner", "username", username));
	}

	@Transactional(readOnly = true)
	public Vet findVetByUser(int userId) {
		return userRepository.findVetByUser(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Vet", "id", userId));
	}

	@Transactional(readOnly = true)
	public Owner findOwnerByUser(int id) {
		return userRepository.findOwnerByUser(id).orElseThrow(() -> new ResourceNotFoundException("Owner", "ID", id));
	}

	@Transactional(readOnly = true)
	public Player findPlayerByUser(int userId) {
		return userRepository.findPlayerByUser(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Player", "id", userId));
	}

	@Transactional(readOnly = true)
	public User findCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null)
			throw new ResourceNotFoundException("Nobody authenticated!");
		else
			return userRepository.findByUsername(auth.getName())
					.orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
	}

	public Boolean existsUser(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional(readOnly = true)
	public Iterable<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Page<User> findAllAdmin(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	

	public Iterable<User> findAllByAuthority(String auth) {
		return userRepository.findAllByAuthority(auth);
	}

	@Transactional
	public User updateUser(@Valid User user, Integer idToUpdate) {
		User toUpdate = findUser(idToUpdate);
		BeanUtils.copyProperties(user, toUpdate, "id");
		userRepository.save(toUpdate);

		return toUpdate;
	}

	@Transactional
	public void deleteUser(Integer id) throws IllegalArgumentException {
		User toDelete = findUser(id);
		Player player = toDelete.getPlayer();

		try {
			if (player != null) {
				deleteThingsPlayer(player);
			}

			userRepository.delete(toDelete);
		} catch (Exception e) {
			throw new IllegalArgumentException("No puedes borrar un jugador que esta en una partida");
		}

	}

	@Transactional
	private void deleteThingsPlayer(Player player) {
		if (player.getFriends() != null) {
			List<Player> friends = player.getFriends();
			for (Player p : friends) {
				List<Player> aux = p.getFriends();
				aux.remove(player);
				playerRepository.save(p);
			}
			player.getFriends().clear();
			playerRepository.save(player);
		}

		if (player.getCards() != null) {
			player.getCards().clear();
			playerRepository.save(player);
		}
	}
}
