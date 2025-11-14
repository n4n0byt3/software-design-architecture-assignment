package uk.ac.mmu.game.domain;

/**
 * Rolls two single dice and sums them (2–12).
 * Implemented as a Singleton.
 */
public final class RandomDoubleDiceShaker implements DiceShaker {

    public static final RandomDoubleDiceShaker INSTANCE = new RandomDoubleDiceShaker();

    private RandomDoubleDiceShaker() {
    }

    @Override
    public int shake() {
        return RandomSingleDiceShaker.INSTANCE.shake()
                + RandomSingleDiceShaker.INSTANCE.shake();
    }
}
