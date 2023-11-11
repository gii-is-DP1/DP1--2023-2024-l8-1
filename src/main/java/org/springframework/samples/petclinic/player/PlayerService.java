package org.springframework.samples.petclinic.player;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerService {
    private PlayerRepository pr;

    @Autowired
    public PlayerService(PlayerRepository pr){
        this.pr=pr;
    }

    @Transactional(readOnly = true)
    public Iterable<Player> findAll(){
        return pr.findAll();
    }

    @Transactional(readOnly = true)
    public Player findPlayerById(int id){
        return pr.findById(id).orElseThrow(()-> new ResourceNotFoundException("Player", "ID", id));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByUser(int userId){
        Optional<Player> p=pr.findPlayerByUser(userId);
        return p.isEmpty()?null:p.get();
    }

    @Transactional(readOnly = true)
    public PlayerRol findPlayerRol(int playerId){
        return pr.findPlayerRol(playerId);
    }

    @Transactional(readOnly = true)
    public Boolean findStartPlayer(int playerId){
        return pr.findStartPlayer(playerId);
    }

    @Transactional(readOnly = true)
    public Integer findScore(int playerId){
        return pr.findScore(playerId);
    }

    @Transactional
    public Player savePlayer(Player p){
        pr.save(p);
        return p;
    }
    
    @Transactional
    public Player updatePlayer(Player p,int id){
        Player toUpdate=findPlayerById(id);
        BeanUtils.copyProperties(p,toUpdate,"id","user");
        return savePlayer(toUpdate);
    }

    @Transactional
    public void deletePlayer(int id){
        Player toDelete=findPlayerById(id);
        pr.delete(toDelete);
    }
    
}
