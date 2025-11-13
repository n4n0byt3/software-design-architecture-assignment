package uk.ac.mmu.game.domain;

import java.util.Random;

/**
 * Generates a random number between 1 and 6.
 */
public class RandomSingleDiceShaker implements DiceShaker {
    private final Random random = new Random();

    @Override
    public int shake() {
        return random.nextInt(6) + 1;
    }
}
