package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for building Game instances for both 2-player and 4-player
 * configurations, small or large boards, and from saved snapshots.
 *
 * This is a use case layer construct that wires domain objects
 * together but does not depend on infrastructure.
 */
public class GameFactory {

    /** Build a 2-player game on the small board (18,3). */
    public Game create2P(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        return createGame(18, 3, 2, singleDie, exactEnd, forfeitOnHit);
    }

    /**
     * Build a game given board sizes and number of players.
     * mainSize = 18 or 36, tailSize = 3 or 6, players = 2 or 4.
     */
    public Game createGame(int mainSize,
                           int tailSize,
                           int players,
                           boolean singleDie,
                           boolean exactEnd,
                           boolean forfeitOnHit) {

        DiceShaker base = singleDie
                ? RandomSingleDiceShaker.INSTANCE
                : RandomDoubleDiceShaker.INSTANCE;

        // Wrap in RecordingDiceShaker so we can save rolls later
        DiceShaker dice = new RecordingDiceShaker(base);

        Board board = new Board(mainSize, tailSize);

        List<Player> ps = new ArrayList<>();
        if (players == 2) {
            ps.add(new Player("Red", 1, "R"));
            ps.add(new Player("Blue", 10, "B"));
        } else if (players == 4) {
            // large board spec: 1, 10, 19, 28
            ps.add(new Player("Red", 1, "R"));
            ps.add(new Player("Blue", 10, "B"));
            ps.add(new Player("Green", 19, "G"));
            ps.add(new Player("Yellow", 28, "Y"));
        } else {
            throw new IllegalArgumentException("players must be 2 or 4");
        }

        Rules rules = new BasicRules();
        if (exactEnd) {
            rules = new ExactEndDecorator(rules);
        }
        if (forfeitOnHit) {
            rules = new ForfeitOnHitDecorator(rules);
        }

        return new Game(board, ps, rules, dice);
    }

    /**
     * Build a game from a saved snapshot using FixedSeqShaker
     * for exact replay of the dice rolls.
     */
    public Game createFromSave(GameSave s) {
        int[] rolls = s.rolls.stream().mapToInt(Integer::intValue).toArray();
        DiceShaker dice = new FixedSeqShaker(rolls);
        Board board = new Board(s.mainSize, s.tailSize);

        List<Player> ps = new ArrayList<>();
        if (s.players == 2) {
            ps.add(new Player("Red", 1, "R"));
            ps.add(new Player("Blue", 10, "B"));
        } else {
            ps.add(new Player("Red", 1, "R"));
            ps.add(new Player("Blue", 10, "B"));
            ps.add(new Player("Green", 19, "G"));
            ps.add(new Player("Yellow", 28, "Y"));
        }

        Rules rules = new BasicRules();
        if (s.exactEnd) {
            rules = new ExactEndDecorator(rules);
        }
        if (s.forfeitOnHit) {
            rules = new ForfeitOnHitDecorator(rules);
        }

        return new Game(board, ps, rules, dice);
    }
}
