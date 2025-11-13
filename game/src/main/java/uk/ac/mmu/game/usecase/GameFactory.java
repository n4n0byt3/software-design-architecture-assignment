package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.*;

import java.util.List;

public class GameFactory {

    public Game create2P(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        DiceShaker dice = singleDie ? new RandomSingleDiceShaker()
                : new RandomDoubleDiceShaker();
        return create2PWithDice(dice, exactEnd, forfeitOnHit);
    }

    public Game create2PWithDice(DiceShaker dice, boolean exactEnd, boolean forfeitOnHit) {
        Board board = new Board(18, 3);
        Player red  = new Player("Red", 1, "R");
        Player blue = new Player("Blue",10,"B");
        Rules rules = new BasicRules();
        if (exactEnd) rules = new ExactEndDecorator(rules);
        if (forfeitOnHit) rules = new ForfeitOnHitDecorator(rules);
        return new Game(board, List.of(red, blue), rules, dice);
    }
}
