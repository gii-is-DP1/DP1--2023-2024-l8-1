package org.springframework.samples.petclinic.game;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.player.PlayerRol;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

@Service
public class GameService {
    
    GameRepository repo;
    UserService userService;
    PlayerService playerService;

    @Autowired
    public GameService(GameRepository repo, UserService userService, PlayerService playerService){
        this.repo=repo;
        this.userService=userService;
        this.playerService=playerService;
    }
    
    @Transactional(readOnly = true)    
    List<Game> getGames(){
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Game> getPublicas(){
        List<Game> result = repo.findPublicas();
        return result;
    }


    @Transactional(readOnly = true)
    public Game getById(int id){
        Optional<Game> result = repo.findById(id);
        return result.isPresent()?result.get():null;
    }

    @Transactional(readOnly = true)
    public Game findByName(String name){
        Optional<Game> result = repo.findByName(name);
        return result.isPresent()?result.get():null;
    }

    @Transactional(readOnly = true)
    public List<Player> findGamePlayers(String name) throws AccessDeniedException{
        return repo.findGamePlayers(findByName(name).getId());
    }

    @Transactional
    public Game createGame(@Valid Game newGame) {
        Player host = userService.findPlayerByUser(userService.findCurrentUser().getId());
        host.setRol(PlayerRol.HOST);
        newGame.setHost(host);
        return repo.save(newGame);
    }

    @Transactional
    public Game saveGame(@Valid Game newGame) {
        return repo.save(newGame);
    }

    @Transactional
	public Game updateGame(Game game, int id) {
		Game toUpdate = getById(id);
		BeanUtils.copyProperties(game, toUpdate, "id");
		return saveGame(toUpdate);
	}
    
    @Transactional
    public Game startGame(String name) throws BadRequestException{
        Game game = findByName(name);
        if (game.getPlayers().size() != 2){
            throw new BadRequestException("La sala debe estar completa antes de empezar la partida");
        }else{
            game.setState(GameState.START_PLAYER_CHOICE);
        }
        return saveGame(game);
    }

    @Transactional
    public Game joinPlayer(String name) {
        Game toUpdate = findByName(name);
        Player me = userService.findPlayerByUser(userService.findCurrentUser().getId());
        List<Player> aux = toUpdate.getPlayers();
        aux.add(me);
        toUpdate.setPlayers(aux);
        me.setRol(PlayerRol.GUEST);
        playerService.updatePlayer(me, me.getId());
        return updateGame(toUpdate, toUpdate.getId());

    }

    @Transactional
    public Game kickPlayer(String name, int id) {
        Game toUpdate = findByName(name);
        List<Player> aux = toUpdate.getPlayers();
        aux.remove(playerService.findPlayerById(id));
        toUpdate.setPlayers(aux);
        return updateGame(toUpdate, toUpdate.getId());

    }

    @Transactional
    public void deleteGameById(int id){
        Game toDelete = getById(id);
        repo.delete(toDelete);
    }

}
