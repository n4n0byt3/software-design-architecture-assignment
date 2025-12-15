package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.RecordingDiceShaker;

import java.util.UUID;

/**
 * Use case: play a game to completion, then save it for replay.
 *
 * Responsibilities:
 * - Build a Game via GameFactory
 * - Attach output observer (port)
 * - Run until GameOver
 * - Demonstrate GameOver state behaviour (attempt extra turns)
 * - Save a replay snapshot (config + dice rolls)
 */
public class PlayGameUseCase {

    private final GameFactory factory;
    private final GameOutputPort output;
    private final GameSaveRepository repository;
    private final GameEventMediator mediator;

    public PlayGameUseCase(GameFactory factory,
                           GameOutputPort output,
                           GameSaveRepository repository,
                           GameEventMediator mediator) {
        this.factory = factory;
        this.output = output;
        this.repository = repository;
        this.mediator = mediator;
    }

    public void execute(int mainSize,
                        int tailSize,
                        int players,
                        boolean singleDie,
                        boolean exactEnd,
                        boolean forfeitOnHit) throws Exception {

        Game game = factory.createGame(mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit);

        // Output is a port (interface) and also a domain observer.
        game.addObserver(output);
        output.setBoard(game.getBoard());

        mediator.event("Starting game");
        output.printConfig(String.format(
                "Board positions=%d, Tail positions=%d, Players=%d, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit
        ));

        while (!game.isOver()) {
            game.playTurn();
        }

        // Demonstrate GameOver behaviour:
        // further turns should return the sentinel result.
        // Print "Game over" for each extra attempt (matches appendix example style).
        for (int i = 0; i < 2; i++) {
            MoveResult extra = game.playTurn();
            if ("Game over".equals(extra.note())) {
                output.printGameOver();
            }
        }

        mediator.event("Finished game");

        // Save using recorded rolls (RecordingDiceShaker decorates the dice).
        if (game.getDice() instanceof RecordingDiceShaker rec) {
            GameSave save = new GameSave(
                    null,
                    mainSize,
                    tailSize,
                    players,
                    singleDie,
                    exactEnd,
                    forfeitOnHit,
                    rec.getRolls()
            );

            UUID id = repository.save(save);
            output.printConfig("Saved game id: " + id);
        } else {
            output.printConfig("Note: dice were not recordable; game not saved.");
        }
    }
}
