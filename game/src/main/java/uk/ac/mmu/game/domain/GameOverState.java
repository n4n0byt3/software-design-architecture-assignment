package uk.ac.mmu.game.domain;

public final class GameOverState implements GameState {

    @Override
    public String name() {
        return "GameOver";
    }

    @Override
    public MoveResult playTurn(Game game) {
        // No more play permitted; return sentinel
        return MoveResult.gameOver();
    }
}
