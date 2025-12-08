package uk.ac.mmu.game.usecase;

import java.util.List;
import java.util.UUID;

/**
 * Snapshot of a finished game sufficient to reproduce its output via replay.
 */
public class GameSave {

    public UUID id;
    public int mainSize;
    public int tailSize;
    public int players;      // 2 or 4
    public boolean singleDie;
    public boolean exactEnd;
    public boolean forfeitOnHit;
    public List<Integer> rolls; // sequence in play order

    public GameSave() { }

    public GameSave(UUID id,
                    int mainSize,
                    int tailSize,
                    int players,
                    boolean singleDie,
                    boolean exactEnd,
                    boolean forfeitOnHit,
                    List<Integer> rolls) {
        this.id = id;
        this.mainSize = mainSize;
        this.tailSize = tailSize;
        this.players = players;
        this.singleDie = singleDie;
        this.exactEnd = exactEnd;
        this.forfeitOnHit = forfeitOnHit;
        this.rolls = rolls;
    }
}
