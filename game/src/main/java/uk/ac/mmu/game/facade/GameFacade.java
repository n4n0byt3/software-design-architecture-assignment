package uk.ac.mmu.game.usecase;

/**
 * A Façade providing a simple high-level API to run a game,
 * hiding the complexities of the domain, strategies, decorators,
 * observers and mediator behind a single method call.
 */
public class GameFacade {

    private final PlayGameUseCase playGame;

    public GameFacade(PlayGameUseCase playGame) {
        this.playGame = playGame;
    }

    /**
     * Runs a full game with the selected variations.
     */
    public void runGame(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        playGame.execute(singleDie, exactEnd, forfeitOnHit);
    }
}
