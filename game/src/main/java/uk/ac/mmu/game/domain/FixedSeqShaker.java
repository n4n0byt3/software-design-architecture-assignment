package uk.ac.mmu.game.domain;

/** Dice that plays back a fixed sequence of rolls (for replay or tests). */
public final class FixedSeqShaker implements DiceShaker {

    private final int[] seq;
    private int index = 0;

    public FixedSeqShaker(int... seq) {
        if (seq == null || seq.length == 0) {
            throw new IllegalArgumentException("At least one roll is required");
        }
        this.seq = seq.clone();
    }

    @Override
    public int shake() {
        int v = seq[index % seq.length];
        index++;
        return v;
    }
}
