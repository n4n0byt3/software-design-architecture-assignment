package uk.ac.mmu.game.domain;

/**
 * DiceShaker that returns a fixed sequence of rolls.
 * Useful for deterministic testing and replaying example games.
 */
public class FixedSequenceDiceShaker implements DiceShaker {

    private final int[] rolls;
    private int index = 0;

    /**
     * @param rolls a non-empty sequence of dice rolls to cycle through
     */
    public FixedSequenceDiceShaker(int... rolls) {
        if (rolls == null || rolls.length == 0) {
            throw new IllegalArgumentException("At least one roll value is required");
        }
        this.rolls = rolls.clone();
    }

    @Override
    public int shake() {
        int value = rolls[index];
        index = (index + 1) % rolls.length; // loop when we reach the end
        return value;
    }
}
