package org.springframework.samples.petclinic.game;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.phase.PhaseService;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.round.RoundService;
import org.springframework.samples.petclinic.ship.ShipService;
import org.springframework.samples.petclinic.turn.TurnService;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.samples.petclinic.vet.VetService;
import org.springframework.transaction.TransactionSystemException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
public class SocialGameServiceTests {

    GameService gs;

    @Mock
    GameRepository gr;

    @Mock
    UserService us;

    @Mock
    PlayerService ps;

    @Mock
    RoundService rs;

    @Mock
    PhaseService phs;

    @Mock
    TurnService ts;

    @Mock
    VetService vs;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setup() {
       // gs = new GameService(gr, us, ps, rs, phs, ts);
    }

    private Game createValidGame(int id) {

        Game newGame = new Game();

        newGame.setId(id);
        newGame.setName("partidaTest");
        newGame.setPublica(true);
        newGame.setState(GameState.LOBBY);
        newGame.setStartTime(LocalDateTime.now());

        return newGame;
    }

    @Test
    public void shouldSaveGame() {
        Game game60 = createValidGame(60);
        Player player = new Player();
        game60.setHost(player);
        try {
            gs.saveGame(game60);
        } catch (Exception e) {
            fail("No exception should be thrown: " + e.getMessage());
        }
    }

    private Game createAGameWithoutName(int id) {

        Game newGame = new Game();

        newGame.setId(id);
        newGame.setPublica(true);
        newGame.setState(GameState.LOBBY);
        newGame.setStartTime(LocalDateTime.now());

        return newGame;

    }

     @Test
    public void shouldNotSaveGame() {

        Game newGame = createAGameWithoutName(61);
        Player player = new Player();
        newGame.setHost(player);

        // Configuración del error esperado
        Set<ConstraintViolation<Game>> violations = new HashSet<>();
        ConstraintViolation<Game> violation = mock(ConstraintViolation.class);
        violations.add(violation);

        when(validator.validate(newGame)).thenReturn(violations);

        // Lanzar manualmente la excepción esperada
        doThrow(new ConstraintViolationException(violations))
                .when(gr).save(newGame);

        // Ejecuta la prueba
        assertThrows(TransactionSystemException.class, () -> gs.saveGame(newGame));

        // Verifica que se haya intentado validar la entidad
        verify(validator, times(1)).validate(newGame);
        // Verifica que se haya intentado guardar la entidad
        verify(gr, times(1)).save(newGame);
    }
    
}