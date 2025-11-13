package uk.ac.mmu.game.domain;

public class ReadyState implements GameState {

    @Override
    public String name() { return "Ready"; }

    @Override
    public MoveResult playTurn(Game game) {
        // First call transitions to InPlay, then immediately play a turn.
        game.switchTo(new InPlayState());
        return game.getState().playTurn(game);
    }
}
