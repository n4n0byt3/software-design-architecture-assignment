package uk.ac.mmu.game.usecase;

import org.junit.jupiter.api.Test;
import uk.ac.mmu.game.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for PlayGameUseCase.
 * We mock GameFactory so that we can inject a deterministic Game instance,
 * and we use in-memory test doubles for persistence and mediator events.
 */
class PlayGameUseCaseTest {

    private static final class InMemoryGameSaveRepository implements GameSaveRepository {
        private GameSave lastSaved;

        @Override
        public UUID save(GameSave save) {
            lastSaved = save;
            if (save.id == null) {
                save.id = UUID.randomUUID();
            }
            return save.id;
        }

        @Override
        public GameSave load(UUID id) {
            throw new UnsupportedOperationException("load() not needed for this test");
        }

        @Override
        public List<UUID> listIds() {
            return lastSaved == null || lastSaved.id == null
                    ? Collections.emptyList()
                    : List.of(lastSaved.id);
        }

        @Override
        public List<GameSave> listAll() {
            return lastSaved == null ? List.of() : List.of(lastSaved);
        }

        GameSave lastSaved() {
            return lastSaved;
        }
    }

    private static final class RecordingMediator implements GameEventMediator {
        private final List<String> events = new ArrayList<>();

        @Override
        public void event(String message) {
            events.add(message);
        }

        boolean contains(String msg) {
            return events.contains(msg);
        }
    }

    /**
     * Silent output port to avoid console noise and keep tests deterministic.
     * Still acts as the Observer attached by the use case.
     */
    private static final class SilentOutputPort implements GameOutputPort {
        @Override public void printTurn(MoveResult result, int turnsForPlayer, Player playerCtx) { }
        @Override public void printWinner(String playerName, int totalTurns, int winnerTurns) { }
        @Override public void onStateChanged(Game game, String from, String to) { }
        @Override public void onTurnPlayed(Game game, MoveResult result, Player currentPlayer) { }
        @Override public void onGameFinished(Game game, Player winner, int totalTurns, int winnerTurns) { }
    }

    @Test
    void executePlaysGameToCompletionAndSavesSnapshot() throws Exception {
        // Arrange a deterministic game
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        DiceShaker base = new FixedSeqShaker(12, 12, 7, 8);
        RecordingDiceShaker recordingDice = new RecordingDiceShaker(base);

        Game game = new Game(board, List.of(red, blue), new BasicRules(), recordingDice);

        // Factory mocked: always return our deterministic game
        GameFactory factory = mock(GameFactory.class);
        when(factory.createGame(anyInt(), anyInt(), anyInt(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(game);

        InMemoryGameSaveRepository repo = new InMemoryGameSaveRepository();
        RecordingMediator mediator = new RecordingMediator();
        SilentOutputPort out = new SilentOutputPort();

        PlayGameUseCase useCase = new PlayGameUseCase(factory, out, repo, mediator);

        // Act
        useCase.execute(18, 3, 2, false, false, false);

        // Assert snapshot persisted
        GameSave saved = repo.lastSaved();
        assertNotNull(saved, "GameSave should have been persisted");
        assertEquals(18, saved.mainSize);
        assertEquals(3, saved.tailSize);
        assertEquals(2, saved.players);
        assertEquals(List.of(12, 12, 7, 8), saved.rolls, "Should save the recorded roll sequence");

        // Assert mediator events
        assertTrue(mediator.contains("Starting game"));
        assertTrue(mediator.contains("Finished game"));

        // Assert factory was used
        verify(factory, times(1)).createGame(18, 3, 2, false, false, false);
    }
}
