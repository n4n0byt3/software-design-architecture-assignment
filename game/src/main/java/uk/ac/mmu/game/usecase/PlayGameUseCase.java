package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.domain.RecordingDiceShaker;

import java.util.UUID;

/**
 * Use case: play a game from configuration through to completion,
 * then save it for later replay.
 *
 * This is an application-level orchestration that:
 * - chooses Strategies (via {@link GameFactory}) based on config flags
 * - drives the Game state machine until completion
 * - triggers persistence through the {@link GameRepository} port
 * - pushes messages out through the {@link OutputPort} and {@link GameMediator}
 */
public final class PlayGameUseCase {

    private final GameFactory factory;
    private final OutputPort out;
    private final GameRepository repo;
    private final GameMediator mediator;

    public PlayGameUseCase(GameFactory factory,
                           OutputPort out,
                           GameRepository repo,
                           GameMediator mediator) {
        this.factory = factory;
        this.out = out;
        this.repo = repo;
        this.mediator = mediator;
    }

    /**
     * Run a game then save it.
     *
     * @param mainSize     18 or 36
     * @param tailSize     3 or 6
     * @param players      2 or 4
     * @param singleDie    true = use single-die strategy, false = double
     * @param exactEnd     true = wrap rules with ExactEndDecorator
     * @param forfeitOnHit true = wrap rules with ForfeitOnHitDecorator
     */
    public void execute(int mainSize,
                        int tailSize,
                        int players,
                        boolean singleDie,
                        boolean exactEnd,
                        boolean forfeitOnHit) throws Exception {

        Game game = factory.createGame(mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit);

        // attach output as observer & give it the board
        game.addObserver(out);
        out.setBoard(game.getBoard());

        mediator.event("Starting game");
        out.printConfig(String.format(
                "Board positions=%d, Tail positions=%d, Players=%d, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit
        ));

        // main loop – state machine handles transitions & winner
        while (!game.isOver()) {
            game.playTurn();
        }

        // Show "Game over" behaviour with two extra rolls in GameOver state
        for (int i = 0; i < 2; i++) {
            MoveResult extra = game.playTurn();
            if ("Game over".equals(extra.note())) {
                out.printGameOver();
            }
        }

        mediator.event("Finished game");

        // Save snapshot using recorded dice sequence (if available)
        RecordingDiceShaker rec = (game.getDice() instanceof RecordingDiceShaker r) ? r : null;
        if (rec != null) {
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
            UUID id = repo.save(save);
            out.printConfig("Saved game id: " + id);
        } else {
            out.printConfig("Note: dice were not recordable; game not saved.");
        }
    }
}
