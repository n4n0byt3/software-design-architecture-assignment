package uk.ac.mmu.game.domain;

/**
 * Dice shaker that plays back a fixed sequence of rolls.
 * Useful for deterministic scenarios and replay.
 */
public class FixedSeqShaker implements DiceShaker {

    private final int[] sequence;
    private int index = 0;

    public FixedSeqShaker(int... sequence) {
        if (sequence == null || sequence.length == 0) {
            throw new IllegalArgumentException("At least one roll is required");
        }
        this.sequence = sequence.clone();
    }

    @Override
    public int shake() {
        int value = sequence[index % sequence.length];
        index++;
        return value;
    }
}
