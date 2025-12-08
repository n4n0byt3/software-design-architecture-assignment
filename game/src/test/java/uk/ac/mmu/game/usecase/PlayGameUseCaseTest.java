package uk.ac.mmu.game.usecase;

import org.junit.jupiter.api.Test;
import uk.ac.mmu.game.domain.*;
import uk.ac.mmu.game.infrastructure.ConsoleOutputAdapter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayGameUseCaseTest {

    private static class InMemoryGameRepository implements GameRepository {
        GameSave last;
        @Override
        public UUID save(GameSave save) {
            last = save;
            if (save.id == null) {
                save.id = UUID.randomUUID();
            }
            return save.id;
        }

        @Override
        public GameSave load(UUID id) {
            throw new UnsupportedOperationException("Not needed for this test");
        }
    }

    private static class RecordingMediator implements GameMediator {
        final java.util.List<String> events = new java.util.ArrayList<>();
        @Override
        public void event(String message) {
            events.add(message);
        }
    }

    private static class StubFactory extends GameFactory {
        private final Game game;

        StubFactory(Game game) {
            this.game = game;
        }

        @Override
        public Game createGame(int mainSize, int tailSize, int players,
                               boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
            return game;
        }
    }

    @Test
    void executePlaysGameToCompletionAndSavesSnapshot() throws Exception {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        DiceShaker base = new FixedSeqShaker(12, 12, 7, 8);
        RecordingDiceShaker rec = new RecordingDiceShaker(base);

        Game game = new Game(board, List.of(red, blue), new BasicRules(), rec);

        InMemoryGameRepository repo = new InMemoryGameRepository();
        RecordingMediator mediator = new RecordingMediator();

        ConsoleOutputAdapter out = new ConsoleOutputAdapter();

        PlayGameUseCase useCase = new PlayGameUseCase(
                new StubFactory(game),
                out,
                repo,
                mediator
        );

        useCase.execute(18, 3, 2, false, false, false);

        assertNotNull(repo.last, "GameSave should have been persisted");
        assertEquals(18, repo.last.mainSize);
        assertEquals(3, repo.last.tailSize);
        assertEquals(2, repo.last.players);
        assertEquals(List.of(12, 12, 7, 8), repo.last.rolls);

        assertTrue(mediator.events.contains("Starting game"));
        assertTrue(mediator.events.contains("Finished game"));
    }
}
