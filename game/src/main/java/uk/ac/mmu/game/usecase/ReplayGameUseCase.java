package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;

import java.util.UUID;

/**
 * Use case: replay a previously-saved game from its snapshot.
 */
public final class ReplayGameUseCase {

    private final GameRepository repo;
    private final GameFactory factory;
    private final OutputPort out;
    private final GameMediator mediator;

    public ReplayGameUseCase(GameRepository repo,
                             GameFactory factory,
                             OutputPort out,
                             GameMediator mediator) {
        this.repo = repo;
        this.factory = factory;
        this.out = out;
        this.mediator = mediator;
    }

    public void replay(UUID id) throws Exception {
        GameSave s = repo.load(id);

        Game game = factory.createFromSave(s);

        game.addObserver(out);
        out.setBoard(game.getBoard());

        mediator.event("Replaying game " + id);
        out.printConfig(String.format(
                "[REPLAY %s] Board positions=%d, Tail positions=%d, Players=%d, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                s.id, s.mainSize, s.tailSize, s.players, s.singleDie, s.exactEnd, s.forfeitOnHit
        ));

        while (!game.isOver()) {
            game.playTurn();
        }

        // show GameOver behaviour for replay as well
        for (int i = 0; i < 2; i++) {
            MoveResult extra = game.playTurn();
            if ("Game over".equals(extra.note())) {
                out.printGameOver();
            }
        }

        mediator.event("Finished replay " + id);
    }
}
