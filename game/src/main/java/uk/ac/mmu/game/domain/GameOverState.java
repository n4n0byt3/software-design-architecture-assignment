package uk.ac.mmu.game.domain;

/**
 * Game over state: any further playTurn calls return a sentinel "Game over" result.
 */
public class GameOverState implements GameState {

    @Override
    public String name() {
        return "GameOver";
    }

    @Override
    public MoveResult playTurn(Game game) {
        return MoveResult.gameOver();
    }
}
