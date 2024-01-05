package org.springframework.samples.petclinic.game;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.BadRequestException;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.round.Round;
import org.springframework.samples.petclinic.round.RoundService;
import org.springframework.samples.petclinic.turn.TurnService;
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
    RoundService roundService;
    PhaseService phaseService;
    TurnService turnService;

    @Autowired
    public GameService(GameRepository repo, UserService userService, PlayerService playerService,
                    RoundService roundService, PhaseService phaseService, TurnService turnService){
        this.repo=repo;
        this.userService=userService;
        this.playerService=playerService;
        this.roundService = roundService;
        this.phaseService = phaseService;
        this.turnService = turnService;
    }

    @Transactional(readOnly = true)
    List<Game> getGames() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Game> getPublicas() {
        List<Game> result = repo.findPublicas();
        return result;
    }

    @Transactional(readOnly = true)
    public Game getById(int id) {
        Optional<Game> result = repo.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    @Transactional(readOnly = true)
    public Game findByName(String name) {
        Optional<Game> result = repo.findByName(name);
        return result.isPresent() ? result.get() : null;
    }

    @Transactional(readOnly = true)
    public List<Player> findGamePlayers(String name) throws AccessDeniedException{
        Game game = findByName(name);
        List<Player> players= new ArrayList<>();
        players.add(game.getHost());
        for (Player player : game.getPlayers()){
            players.add(player);
        }
        return players;
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
        List<Round> rounds = new ArrayList<>();
        game.setState(GameState.START_PLAYER_CHOICE);

        RoundBuilder builder = new RoundBuilder(roundService, phaseService, turnService);
        Director director = new Director(builder, findGamePlayers(name));

        director.InitialRound();

        rounds.add(builder.getRound());
        
        /*Round round = new Round();
        List<Phase> phases = new ArrayList<>();
        /*List<Turn> turns1 = new ArrayList<>();


        Round round = new Round();
        Phase phase = new Phase();
        Phase phase1 = new Phase();        
        
        for (int i = 0; i < 2; i++){
         turns.add(turnService.saveTurn(new Turn()));
        }
        turns1.add(turnService.saveTurn(new Turn()));
        phase.setTurns(turns);
        phase1.setTurns(turns1);
        phaseService.savePhase(phase);
        phases.add(phase);
        phaseService.savePhase(phase1);
        phases.add(phase1);
        turns.clear();

        for (int i = 0; i < 2; i++){
            List<Turn> turns = new ArrayList<>();
            Phase phase = new Phase(); 
            for (int j = 0; j < 3; j++){
                Turn turn = new Turn();
                turn.setPlayer(findGamePlayers(name).get(j)); 
                turnService.saveTurn(turn);
                turns.add(turn);
            }
            phase.setTurns(turns);
            phaseService.savePhase(phase);
            phases.add(phase);
        }

        /*Phase phase = phases.get(0);
        phase.setTurns(turns);
        phaseService.savePhase(phase);
        phases.add(phase);



        round.setPhases(phases);
        roundService.saveRound(round);
        rounds.add(round);*/        

        game.setRounds(rounds);
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
    public void deleteGameById(int id) {
        Game toDelete = getById(id);
        repo.delete(toDelete);
    }

}
