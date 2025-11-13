package uk.ac.mmu.game.domain;

import java.util.Random;

public class RandomDice implements DiceShaker {
    private final boolean singleDie;
    private final Random rnd = new Random();
    public RandomDice(boolean singleDie) { this.singleDie = singleDie; }
    @Override public int shake() {
        if (singleDie) return 1 + rnd.nextInt(6);
        return (1 + rnd.nextInt(6)) + (1 + rnd.nextInt(6));
    }
}
