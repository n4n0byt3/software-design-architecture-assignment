package uk.ac.mmu.game.usecase;

import org.junit.jupiter.api.Test;
import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.RecordingDiceShaker;
import uk.ac.mmu.game.infrastructure.ConsoleOutputAdapter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ReplayGameUseCaseTest {

    private static class InMemoryGameRepository implements GameRepository {
        private final GameSave stored;

        InMemoryGameRepository(GameSave save) {
            this.stored = save;
        }

        @Override
        public UUID save(GameSave save) {
            throw new UnsupportedOperationException("Not needed for this test");
        }

        @Override
        public GameSave load(UUID id) {
            return stored;
        }
    }

    private static class RecordingMediator implements GameMediator {
        final java.util.List<String> events = new java.util.ArrayList<>();
        @Override
        public void event(String message) {
            events.add(message);
        }
    }

    @Test
    void replayLoadsSnapshotAndReplaysToGameOver() throws Exception {
        GameSave save = new GameSave(
                UUID.randomUUID(),
                18,
                3,
                2,
                false,
                false,
                false,
                List.of(12, 12, 7, 8)
        );

        InMemoryGameRepository repo = new InMemoryGameRepository(save);
        GameFactory factory = new GameFactory();
        ConsoleOutputAdapter out = new ConsoleOutputAdapter();
        RecordingMediator mediator = new RecordingMediator();

        ReplayGameUseCase replay = new ReplayGameUseCase(repo, factory, out, mediator);

        replay.replay(save.id);

        assertTrue(
                mediator.events.stream().anyMatch(e -> e.startsWith("Replaying game")),
                "Should log replay start"
        );
        assertTrue(
                mediator.events.stream().anyMatch(e -> e.startsWith("Finished replay")),
                "Should log replay end"
        );
    }
}
