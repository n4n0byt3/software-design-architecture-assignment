package uk.ac.mmu.game.domain;

/** Dice that plays back a fixed sequence of rolls (for replay or tests). */
public class FixedSeqShaker implements DiceShaker {
    private final int[] seq;
    private int i = 0;
    public FixedSeqShaker(int... seq) { this.seq = seq; }
    @Override public int shake() {
        if (seq.length == 0) return 0;
        int v = seq[i % seq.length];
        i++;
        return v;
    }
}
