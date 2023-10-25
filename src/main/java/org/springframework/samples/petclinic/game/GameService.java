package org.springframework.samples.petclinic.game;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;

@Service
public class GameService {
    
    GameRepository repo;

    @Autowired
    public GameService(GameRepository repo){
        this.repo=repo;
    }
    
    @Transactional(readOnly = true)    
    List<Game> getGames(){
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Game getById(int id){
        Optional<Game> result = repo.findById(id);
        return result.isPresent()?result.get():null;
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
    public void deleteGameById(int id){
        Game toDelete = getById(id);
        repo.delete(toDelete);
    }

}
