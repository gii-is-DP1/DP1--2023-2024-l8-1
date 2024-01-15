package org.springframework.samples.petclinic.player;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PlayerService {
    private PlayerRepository pr;
    private UserService us;

    @Autowired
    public PlayerService(PlayerRepository pr, UserService us) {
        this.pr = pr;
        this.us = us;
    }

    @Transactional(readOnly = true)
    public List<Player> findAll() {
        return pr.findAll();
    }

    @Transactional(readOnly = true)
    public List<Player> getFriends() {
        return pr.findFriends((us.findPlayerByUser(us.findCurrentUser().getId())).getId());
    }

    @Transactional(readOnly = true)
    public Player findPlayerById(int id) {
        return pr.findById(id).orElseThrow(() -> new ResourceNotFoundException("Player", "ID", id));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByUser(int userId) {
        Optional<Player> p = pr.findPlayerByUser(userId);
        return p.isEmpty() ? null : p.get();
    }

    @Transactional(readOnly = true)
    public PlayerRol findPlayerRol(int playerId) {
        return pr.findPlayerRol(playerId);
    }

    @Transactional(readOnly = true)
    public Integer findScore(int playerId) {
        return pr.findScore(playerId);
    }

    @Transactional
    public void startSpectating(Player player){
        player.setRol(PlayerRol.SPECTATOR);
        savePlayer(player);
    }

    @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public Player savePlayer(Player p) {
        pr.save(p);
        return p;
    }

    @Transactional
    public void addFriend(Player me, int id) {

        Player source = findPlayerById(id);

        if (!me.getFriends().contains(source) && !source.getFriends().contains(me)) {
            List<Player> myFriends = me.getFriends();
            myFriends.add(source);
            me.setFriends(myFriends);
            savePlayer(me);

            List<Player> sourceFriends = source.getFriends();
            sourceFriends.add(me);
            source.setFriends(sourceFriends);
            savePlayer(source);
        } else {
            String errorMessage = "Error al agregar un nuevo amigo. Ya son amigos.";
            System.out.println(errorMessage);
        }

    }

    @Transactional
    public void deleteFriend(int id) {

        Player me = us.findPlayerByUser(us.findCurrentUser().getId());
        Player source = findPlayerById(id);

        List<Player> myFriends = me.getFriends();
        myFriends.remove(source);
        me.setFriends(myFriends);

        List<Player> sourceFriends = source.getFriends();
        sourceFriends.remove(me);
        source.setFriends(sourceFriends);
    }

    @Transactional
    public Player updatePlayer(Player p, int id) {
        Player toUpdate = findPlayerById(id);
        BeanUtils.copyProperties(p, toUpdate, "id", "user", "friends");
        return savePlayer(toUpdate);
    }

    @Transactional
    public void deletePlayer(int id) {
        Player toDelete = findPlayerById(id);
        pr.delete(toDelete);
    }

}
