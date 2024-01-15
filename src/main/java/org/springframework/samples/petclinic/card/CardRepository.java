package org.springframework.samples.petclinic.card;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends CrudRepository<Card, Integer> {

    @Query("SELECT c FROM Card c WHERE c.player.id = :playerId")
    List<Card> getCardsForPlayer(@Param("playerId") Integer playerId);

    @Query("SELECT c FROM Card c WHERE c.type = :type AND c.player.id = :playerId")
    Card findCardByType(CardType type, Integer playerId);

    @Query("SELECT c FROM Card c WHERE c.performingOrder = :order AND c.player.id = :playerId")
    Card findCardByOrder(Integer order, Integer playerId);

    List<Card> findAll();

}
