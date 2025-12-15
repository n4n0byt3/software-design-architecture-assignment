package uk.ac.mmu.game.domain;

import java.util.Random;

/**
 * Generates a random number between 1 and 6.
 * Implemented as a Singleton (stateless API).
 */
public final class RandomSingleDiceShaker implements DiceShaker {

    public static final RandomSingleDiceShaker INSTANCE = new RandomSingleDiceShaker();

    private final Random random = new Random();

    private RandomSingleDiceShaker() {
    }

    @Override
    public int shake() {
        return random.nextInt(6) + 1;
    }
}
