package uk.ac.mmu.game.domain;

/**
 * Rolls two single dice and sums them (2–12).
 */
public class RandomDoubleDiceShaker implements DiceShaker {
    private final DiceShaker single = new RandomSingleDiceShaker();

    @Override
    public int shake() {
        return single.shake() + single.shake();
    }
}
