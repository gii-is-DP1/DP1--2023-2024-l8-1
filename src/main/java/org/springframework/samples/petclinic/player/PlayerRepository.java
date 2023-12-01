package org.springframework.samples.petclinic.player;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    public List<Player> findAll();

    @Query("SELECT DISTINCT p FROM Player p WHERE p.user.id=:userId")
    public Optional<Player> findPlayerByUser(@Param("userId") int userId);

    @Query("SELECT COUNT(p) FROM Player p")
    public Integer countAll();

    @Query("SELECT p.rol FROM Player p WHERE p.id=:playerId")
    public PlayerRol findPlayerRol(@Param("playerId") int playerId);

    @Query("SELECT p.startPlayer FROM Player p WHERE p.id=:playerId") //Este no debería buscar el jugador que tenga el atributo startPlayer a verdadero dentro de una partida?
    public Boolean findStartPlayer(@Param("playerId") int playerId);
    
    @Query("SELECT p.score FROM Player p WHERE p.id=:playerId")
    public Integer findScore(@Param("playerId") int playerId);

    @Query("SELECT p.friends FROM Player p WHERE p.id = ?1")
    public List<Player> findFriends(int playerId);
}
