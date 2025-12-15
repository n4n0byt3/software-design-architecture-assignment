package uk.ac.mmu.game.domain;

/**
 * Represents the game board:
 * <ul>
 *   <li>A shared main ring of positions (1..mainSize)</li>
 *   <li>A player-specific tail of positions (e.g. R1, R2, R3 (End))</li>
 * </ul>
 *
 * <p>Players track "progress" as an abstract integer in range 0..endProgress:
 * <ul>
 *   <li>0 = Home</li>
 *   <li>1..mainSize-1 = main ring movement</li>
 *   <li>mainSize..endProgress = tail movement</li>
 * </ul>
 *
 * <p>That abstract progress maps to different absolute ring positions depending on a player's home index.
 */
public class Board {

    private final int mainSize;
    private final int tailSize;

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
     * Returns the maximum progress value (i.e. End).
     *
     * <p>Example:
     * small board: 18 + 3 - 1 = 20
     * large board: 36 + 6 - 1 = 41
     */
    public int endProgress() {
        return mainSize + tailSize - 1;
    }

    /**
     * Converts abstract progress into the required label.
     */
    public String labelFor(Player player, int progress) {
        if (progress == 0) {
            return "Home (Position " + player.getHomeIndex() + ")";
        }

        // Main ring: progress < mainSize (because mainSize itself is the first tail square)
        if (progress < mainSize) {
            int ringPos = mainRingPosFor(player, progress);
            return "Position " + ringPos;
        }

        // Tail: progress >= mainSize
        int tailStep = progress - mainSize + 1;
        if (tailStep == tailSize) {
            return player.getColourLetter() + tailStep + " (End)";
        }
        return "Tail Position " + player.getColourLetter() + tailStep;
    }

    /**
     * Computes absolute ring position (1..mainSize) for a given player and ring progress.
     *
     * <p>ringProgress is still the abstract progress value (0..mainSize-1), where:
     * 0 means "on home square on the ring".
     */
    public int mainRingPosFor(Player player, int ringProgress) {
        return ((player.getHomeIndex() - 1 + ringProgress) % mainSize) + 1;
    }
}
