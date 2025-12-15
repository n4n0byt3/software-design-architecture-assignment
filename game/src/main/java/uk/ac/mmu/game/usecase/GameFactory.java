package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for building Game instances for various configurations.
 *
 * <p>Lives in the use case layer: wires domain objects without infrastructure.
 */
public class GameFactory {

    public Game create2P(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        return createGame(18, 3, 2, singleDie, exactEnd, forfeitOnHit);
    }

    public Game createGame(int mainSize,
                           int tailSize,
                           int players,
                           boolean singleDie,
                           boolean exactEnd,
                           boolean forfeitOnHit) {

        DiceShaker baseDice = singleDie
                ? RandomSingleDiceShaker.INSTANCE
                : RandomDoubleDiceShaker.INSTANCE;

        // Decorator to record dice for save/replay.
        DiceShaker dice = new RecordingDiceShaker(baseDice);

        Board board = new Board(mainSize, tailSize);
        List<Player> playerList = buildPlayers(players);

        Rules rules = buildRules(exactEnd, forfeitOnHit);

        return new Game(board, playerList, rules, dice);
    }

    public Game createFromSave(GameSave save) {
        int[] rolls = save.rolls.stream().mapToInt(Integer::intValue).toArray();
        DiceShaker dice = new FixedSeqShaker(rolls);

        Board board = new Board(save.mainSize, save.tailSize);
        List<Player> players = buildPlayers(save.players);

        Rules rules = buildRules(save.exactEnd, save.forfeitOnHit);

        return new Game(board, players, rules, dice);
    }

    private static List<Player> buildPlayers(int players) {
        List<Player> ps = new ArrayList<>();

        if (players == 2) {
            ps.add(new Player("Red", 1, "R"));
            ps.add(new Player("Blue", 10, "B"));
            return ps;
        }

        if (players == 4) {
            ps.add(new Player("Red", 1, "R"));
            ps.add(new Player("Blue", 10, "B"));
            ps.add(new Player("Green", 19, "G"));
            ps.add(new Player("Yellow", 28, "Y"));
            return ps;
        }

        throw new IllegalArgumentException("players must be 2 or 4");
    }

    private static Rules buildRules(boolean exactEnd, boolean forfeitOnHit) {
        Rules rules = new BasicRules();
        if (exactEnd) rules = new ExactEndDecorator(rules);
        if (forfeitOnHit) rules = new ForfeitOnHitDecorator(rules);
        return rules;
    }
}
