package uk.ac.mmu.game.domain;

/**
 * Ready state: the first call transitions to InPlay and performs the first turn.
 */
public class ReadyState implements GameState {

    @Override
    public String name() {
        return "Ready";
    }

    @Override
    public MoveResult playTurn(Game game) {
        game.switchTo(new InPlayState());
        return game.playTurn();
    }
}
