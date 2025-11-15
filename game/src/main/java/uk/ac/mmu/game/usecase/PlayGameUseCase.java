package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.Player;

/**
 * Application service (use case) that runs a complete game session.
 *
 * It depends only on abstractions:
 *  - {@link GameFactory} to create a Game (policy over construction).
 *  - {@link OutputPort} to present information (port).
 *  - {@link GameMediator} to report high-level events (optional mediator).
 *
 * Concrete implementations (console adapter, specific factories, etc.)
 * are provided by the DI container (Spring), satisfying the
 * Dependency Inversion Principle and keeping this class framework-free.
 */
public class PlayGameUseCase {

    private final GameFactory factory;
    private final OutputPort out;
    private final GameMediator mediator;

    public PlayGameUseCase(GameFactory factory, OutputPort out, GameMediator mediator) {
        this.factory = factory;
        this.out = out;
        this.mediator = mediator;
    }

    /**
     * @param singleDie      true = 1xD6, false = 2xD6
     * @param exactEnd       true = must land exactly on END or forfeit
     * @param forfeitOnHit   true = stay put if landing square is occupied
     */
    public void execute(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        // Create the game via the injected factory (no concrete coupling here).
        Game game = factory.createGame(singleDie, exactEnd, forfeitOnHit);

        // Configure the presenter and register it as an observer.
        // OutputPort extends GameObserver and exposes setBoard(...) as a port method.
        out.setBoard(game.getBoard());
        game.addObserver(out);

        String cfg = String.format(
                "Board positions=%d, Tail positions=%d, Players=%s, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                game.getBoard().mainSize(),
                game.getBoard().tailSize(),
                game.getPlayers().stream().map(Player::getName).toList(),
                singleDie, exactEnd, forfeitOnHit
        );

        out.printConfig(cfg);
        mediator.event("Starting new game: " + cfg);

        // Main loop: all side-effects (printing, logging) are now triggered
        // by observers registered on the Game.
        while (!game.isOver()) {
            game.playTurn();
        }

        // Winner summary is handled by observers; here we just log via the mediator.
        game.winner().ifPresentOrElse(
                w -> mediator.event("Game finished, winner: " + w.getName()),
                () -> mediator.event("Game finished, no winner.")
        );
    }
}
