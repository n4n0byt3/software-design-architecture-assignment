package uk.ac.mmu.game.usecase;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for ReplayGameUseCase.
 * Uses an in-memory repository to return a prepared GameSave snapshot.
 */
class ReplayGameUseCaseTest {

    private static final class InMemoryGameSaveRepository implements GameSaveRepository {
        private final GameSave stored;

        InMemoryGameSaveRepository(GameSave stored) {
            this.stored = stored;
        }

        @Override
        public UUID save(GameSave save) {
            throw new UnsupportedOperationException("save() not needed for this test");
        }

        @Override
        public GameSave load(UUID id) {
            return stored;
        }

        @Override
        public List<UUID> listIds() {
            return (stored == null || stored.id == null) ? Collections.emptyList() : List.of(stored.id);
        }

        @Override
        public List<GameSave> listAll() {
            return stored == null ? List.of() : List.of(stored);
        }
    }

    private static final class RecordingMediator implements GameEventMediator {
        private final List<String> events = new ArrayList<>();

        @Override
        public void event(String message) {
            events.add(message);
        }

        boolean anyStartsWith(String prefix) {
            return events.stream().anyMatch(e -> e.startsWith(prefix));
        }
    }

    private static final class SilentOutputPort implements GameOutputPort {
        @Override public void printTurn(uk.ac.mmu.game.domain.MoveResult result, int turnsForPlayer, uk.ac.mmu.game.domain.Player playerCtx) { }
        @Override public void printWinner(String playerName, int totalTurns, int winnerTurns) { }
        @Override public void onStateChanged(uk.ac.mmu.game.domain.Game game, String from, String to) { }
        @Override public void onTurnPlayed(uk.ac.mmu.game.domain.Game game, uk.ac.mmu.game.domain.MoveResult result, uk.ac.mmu.game.domain.Player currentPlayer) { }
        @Override public void onGameFinished(uk.ac.mmu.game.domain.Game game, uk.ac.mmu.game.domain.Player winner, int totalTurns, int winnerTurns) { }
    }

    @Test
    void replayLoadsSnapshotAndEmitsStartAndFinishEvents() throws Exception {
        UUID id = UUID.randomUUID();

        GameSave save = new GameSave(
                id,
                18,
                3,
                2,
                false,
                false,
                false,
                List.of(12, 12, 7, 8)
        );

        InMemoryGameSaveRepository repo = new InMemoryGameSaveRepository(save);
        GameFactory factory = new GameFactory();
        SilentOutputPort out = new SilentOutputPort();
        RecordingMediator mediator = new RecordingMediator();

        ReplayGameUseCase useCase = new ReplayGameUseCase(repo, factory, out, mediator);

        useCase.replay(id);

        assertTrue(mediator.anyStartsWith("Replaying game"), "Should emit replay start event");
        assertTrue(mediator.anyStartsWith("Finished replay"), "Should emit replay finished event");
    }
}
