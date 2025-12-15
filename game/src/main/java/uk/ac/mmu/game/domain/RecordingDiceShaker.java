package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Decorator that records every roll produced by the delegate.
 * Used for game save / replay (store config + roll sequence).
 */
public class RecordingDiceShaker implements DiceShaker {

    private final DiceShaker delegate;
    private final List<Integer> rolls = new ArrayList<>();

    public RecordingDiceShaker(DiceShaker delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate is required");
        }
        this.delegate = delegate;
    }

    @Override
    public int shake() {
        int value = delegate.shake();
        rolls.add(value);
        return value;
    }

    public List<Integer> getRolls() {
        return Collections.unmodifiableList(rolls);
    }
}
