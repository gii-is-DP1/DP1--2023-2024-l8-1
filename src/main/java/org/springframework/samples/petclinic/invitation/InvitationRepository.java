package org.springframework.samples.petclinic.invitation;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.game.Game;

public interface InvitationRepository extends CrudRepository<Invitation,Integer>{

    @Query("SELECT i.discriminator FROM Invitation i WHERE i.id=?1")
    public InvitationType typeInvitation(int id);

    //NO SE SI ESTO SE DEBE HACER
    @Query("SELECT i.game FROM Invitation i WHERE i.game.id=?1")
    public Game findGame(int id);

    @Query("SELECT i FROM Invitation i WHERE i.playerTarget.id=:playerId")
    public List<Invitation> findInvitationsForPlayerTarget(@Param("playerId") int playerId);

    @Query("SELECT i FROM Invitation i WHERE i.playerSource.id=:playerId")
    public List<Invitation> findInvitationsForPlayerSource(@Param("playerId") int playerId);
    
}
