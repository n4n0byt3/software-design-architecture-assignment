package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Decorator that records every roll produced by the delegate. */
public class RecordingDiceShaker implements DiceShaker {
    private final DiceShaker delegate;
    private final List<Integer> rolls = new ArrayList<>();

    public RecordingDiceShaker(DiceShaker delegate) { this.delegate = delegate; }

    @Override public int shake() {
        int v = delegate.shake();
        rolls.add(v);
        return v;
    }

    public List<Integer> getRolls() { return Collections.unmodifiableList(rolls); }
}
