package org.springframework.samples.petclinic.round;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.phase.Phase;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.sector.Sector;
import org.springframework.samples.petclinic.sector.SectorService;
import org.springframework.samples.petclinic.turn.Turn;
import org.springframework.samples.petclinic.turn.TurnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoundService {

    private RoundRepository roundRepository;
    private PhaseService phaseService;
    private SectorService sectorService;
    private TurnService turnService;

    @Autowired
    public RoundService(RoundRepository roundRepository, PhaseService phaseService, SectorService sectorService,
            TurnService turnService) {
        this.roundRepository = roundRepository;
        this.phaseService = phaseService;
        this.sectorService = sectorService;
        this.turnService = turnService;
    }

    @Transactional(readOnly = true)
    public Round findById(int id) {
        Optional<Round> result = roundRepository.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    @Transactional
    public void roundIsOver(Round round, Phase phase, Game game, Player triPrimePlayer) {
        if (phase.getTurns().stream().allMatch(s -> s.getIsOver())) {
            if (phase.getTurns().size() == 3 && triPrimePlayer != null) {
                Turn turn = new Turn();
                turn.setIsOver(false);
                turn.setPlayer(triPrimePlayer);
                List<Turn> ls = phase.getTurns();
                ls.add(turn);
                phase.setTurns(ls);
                turnService.saveTurn(turn);
                phaseService.savePhase(phase);
            } else {
                phase.setIsOver(true);
                phaseService.savePhase(phase);
                if (phase.getIsPoint()) {
                    for (Sector sector : game.getGameBoard().getSectors()) {
                        sector.setIsScored(false);
                        sectorService.save(sector);
                    }
                }
            }
        }
        if (round.getPhases().stream().allMatch(s -> s.getIsOver())) {
            round.setIsOver(true);
            saveRound(round);
        }
    }

    @Transactional
    public Round saveRound(Round round) {
        return roundRepository.save(round);
    }

}
