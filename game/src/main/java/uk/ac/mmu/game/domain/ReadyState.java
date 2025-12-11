package uk.ac.mmu.game.domain;

/**
 * Lifecycle "Ready" state from the GameStates model.
 * First call to playTurn() transitions to InPlay and plays the first turn.
 */
public final class ReadyState implements GameState {

    @Override
    public String name() {
        return "Ready";
    }

    @Override
    public MoveResult playTurn(Game game) {
        game.switchTo(new InPlayState());
        return game.getState().playTurn(game);
    }
}
