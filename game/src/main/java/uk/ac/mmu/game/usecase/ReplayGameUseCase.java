package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;

import java.util.UUID;

/**
 * Use case: replay a previously saved game.
 *
 * Snapshot approach:
 * - load config + sequence of rolls
 * - rebuild a deterministic game using FixedSeqShaker
 * - run it through the normal game engine to reproduce the output
 */
public class ReplayGameUseCase {

    private final GameSaveRepository repository;
    private final GameFactory factory;
    private final GameOutputPort output;
    private final GameEventMediator mediator;

    public ReplayGameUseCase(GameSaveRepository repository,
                             GameFactory factory,
                             GameOutputPort output,
                             GameEventMediator mediator) {
        this.repository = repository;
        this.factory = factory;
        this.output = output;
        this.mediator = mediator;
    }

    public void replay(UUID id) throws Exception {
        GameSave save = repository.load(id);
        Game game = factory.createFromSave(save);

        game.addObserver(output);
        output.setBoard(game.getBoard());

        mediator.event("Replaying game " + id);
        output.printConfig(String.format(
                "[REPLAY %s] Board positions=%d, Tail positions=%d, Players=%d, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                save.id, save.mainSize, save.tailSize, save.players, save.singleDie, save.exactEnd, save.forfeitOnHit
        ));

        while (!game.isOver()) {
            game.playTurn();
        }

        // Demonstrate GameOver behaviour (print once).
        boolean printedGameOver = false;
        for (int i = 0; i < 2; i++) {
            MoveResult extra = game.playTurn();
            if (!printedGameOver && "Game over".equals(extra.note())) {
                output.printGameOver();
                printedGameOver = true;
            }
        }

        mediator.event("Finished replay " + id);
    }
}
