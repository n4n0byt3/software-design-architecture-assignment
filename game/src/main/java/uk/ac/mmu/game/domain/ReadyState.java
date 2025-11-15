package uk.ac.mmu.game.domain;

/**
 * Lifecycle "ReadyToPlay" state from the GameStates model.
 *
 * - Initial lifecycle state of the Game.
 * - First call to playTurn() triggers the "turn" event and
 *   transitions the Game into the InPlay state.
 * - After the transition, the first actual turn is played in InPlay.
 *
 * This maps directly to the state machine:
 *
 *   Current State: ReadyToPlay
 *   Trigger:       turn
 *   Next State:    InPlay
 */
public class ReadyState implements GameState {

    @Override
    public String name() {
        // Use the exact label from the GameStates.docx
        return "ReadyToPlay";
    }

    @Override
    public MoveResult playTurn(Game game) {
        // First call transitions to InPlay, then immediately play a turn
        game.switchTo(new InPlayState());
        // Delegate directly to the new state's playTurn
        return game.getState().playTurn(game);
    }
}
