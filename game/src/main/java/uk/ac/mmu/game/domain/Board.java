package uk.ac.mmu.game.domain;

/**
 * Represents the game board consisting of:
 * - a shared main ring of positions (1..mainSize)
 * - a player-specific tail of positions (e.g. R1, R2, R3 (End)).
 *
 * Contract:
 * - mainSize and tailSize must be > 0.
 * - All progress values passed to this class must be in the range [0, endProgress()].
 *   If not, an IllegalArgumentException is thrown.
 */
public final class Board {

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
     * 0              = Home
     * 1..mainSize-1  = main ring
     * mainSize..end  = tail
     */
    public int endProgress() {
        return mainSize + tailSize - 1;
    }

    /**
     * Convert a player's abstract progress (0..endProgress) into a
     * human-readable label that matches the assignment examples.
     *
     * @param p        player context (must not be null)
     * @param progress abstract progress in [0, endProgress()]
     */
    public String labelFor(Player p, int progress) {
        if (p == null) {
            throw new IllegalArgumentException("player is required");
        }
        validateProgress(progress);

        if (progress == 0) {
            return "Home (Position " + p.getHomeIndex() + ")";
        }
        if (progress < mainSize) {
            int mainPos = mainRingPosFor(p, progress);
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
     *
     * @throws IllegalArgumentException if progress is not in [0, end] or is not on the main ring.
     */
    public int mainRingPosFor(Player p, int progress) {
        if (p == null) {
            throw new IllegalArgumentException("player is required");
        }
        validateProgress(progress);
        if (progress >= mainSize) {
            throw new IllegalArgumentException(
                    "Progress " + progress + " is not on the main ring (mainSize=" + mainSize + ")"
            );
        }
        return ((p.getHomeIndex() - 1 + progress) % mainSize) + 1;
    }

    /**
     * Contract check: progress must always be between 0 and endProgress().
     */
    private void validateProgress(int progress) {
        int end = endProgress();
        if (progress < 0 || progress > end) {
            throw new IllegalArgumentException(
                    "progress out of range: " + progress + " (expected 0.." + end + ")"
            );
        }
    }
}
