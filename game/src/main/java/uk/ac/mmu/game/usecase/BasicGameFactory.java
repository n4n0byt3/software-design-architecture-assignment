package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.*;

import java.util.List;

/**
 * Concrete factory for the standard 2-player game.
 */
public class BasicGameFactory implements GameFactory {

    @Override
    public Game createGame(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        DiceShaker dice = singleDie
                ? RandomSingleDiceShaker.INSTANCE
                : RandomDoubleDiceShaker.INSTANCE;

        return createWithDice(dice, exactEnd, forfeitOnHit);
    }

    /**
     * Factory method that allows injecting any DiceShaker implementation.
     * Used by both the CLI (random dice) and tests/replay (fixed sequence).
     */
    public Game createWithDice(DiceShaker dice, boolean exactEnd, boolean forfeitOnHit) {
        Board board = new Board(18, 3);
        Player red  = new Player("Red", 1, "R");
        Player blue = new Player("Blue",10,"B");
        Rules rules = new BasicRules();
        if (exactEnd) {
            rules = new ExactEndDecorator(rules);
        }
        if (forfeitOnHit) {
            rules = new ForfeitOnHitDecorator(rules);
        }
        return new Game(board, List.of(red, blue), rules, dice);
    }

    /**
     * Convenience factory for creating a 2-player game with a fixed
     * sequence of dice rolls. Ideal for deterministic testing and
     * reproducing example games.
     */
    public Game create2PWithFixedRolls(boolean exactEnd, boolean forfeitOnHit, int... rolls) {
        DiceShaker dice = new FixedSequenceDiceShaker(rolls);
        return createWithDice(dice, exactEnd, forfeitOnHit);
    }
}
