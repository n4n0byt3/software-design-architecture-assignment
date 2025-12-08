package uk.ac.mmu.game.domain;

/**
 * Represents the game board consisting of:
 * - a shared main ring of positions (1..mainSize)
 * - a player-specific tail of positions (e.g. R1, R2, R3 (End)).
 */
public class Board {

    private final int mainSize;
    private final int tailSize;

    /**
     * @param mainSize number of shared ring positions (must be > 0)
     * @param tailSize number of tail positions per player (must be > 0)
     */
    public Board(int mainSize, int tailSize) {
        if (mainSize <= 0) {
            throw new IllegalArgumentException("mainSize must be > 0");
        }
        if (tailSize <= 0) {
            throw new IllegalArgumentException("tailSize must be > 0");
        }
        this.mainSize = mainSize;
        this.tailSize = tailSize;
    }

    public int mainSize() {
        return mainSize;
    }

    public int tailSize() {
        return tailSize;
    }

    /**
     * 0            = Home
     * 1..mainSize-1  = main ring
     * mainSize..end  = tail
     */
    public int endProgress() {
        return mainSize + tailSize - 1;
    }

    /**
     * Convert a player's abstract progress (0..endProgress) into a
     * human-readable label that matches the assignment examples.
     */
    public String labelFor(Player p, int progress) {
        if (progress == 0) {
            return "Home (Position " + p.getHomeIndex() + ")";
        }
        if (progress < mainSize) {
            int mainPos = ((p.getHomeIndex() - 1 + progress) % mainSize) + 1;
            return "Position " + mainPos;
        }
        int tailStep = progress - mainSize + 1;
        if (tailStep == tailSize) {
            return p.getColourLetter() + tailStep + " (End)";
        }
        return "Tail Position " + p.getColourLetter() + tailStep;
    }

    /**
     * Get the absolute main-ring position (1..mainSize) for a player
     * given their progress, assuming progress is on the main ring.
     */
    public int mainRingPosFor(Player p, int progress) {
        return ((p.getHomeIndex() - 1 + progress) % mainSize) + 1;
    }
}
