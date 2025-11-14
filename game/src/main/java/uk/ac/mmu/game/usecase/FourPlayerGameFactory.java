package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.*;

import java.util.List;

/**
 * Example alternative factory.
 * Demonstrates that the system supports new game families without
 * modifying existing code, only by adding new factories.
 */
public class FourPlayerGameFactory implements GameFactory {

    @Override
    public Game createGame(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        DiceShaker dice = singleDie
                ? RandomSingleDiceShaker.INSTANCE
                : RandomDoubleDiceShaker.INSTANCE;

        Board board = new Board(24, 4);

        Player p1 = new Player("Red",    1,  "R");
        Player p2 = new Player("Blue",   7,  "B");
        Player p3 = new Player("Green",  13, "G");
        Player p4 = new Player("Yellow", 19, "Y");

        Rules rules = new BasicRules();
        if (exactEnd) {
            rules = new ExactEndDecorator(rules);
        }
        if (forfeitOnHit) {
            rules = new ForfeitOnHitDecorator(rules);
        }

        return new Game(board, List.of(p1, p2, p3, p4), rules, dice);
    }
}
