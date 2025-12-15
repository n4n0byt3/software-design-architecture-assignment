package uk.ac.mmu.game.domain;

/**
 * Immutable record representing the outcome of a single move.
 */
public record MoveResult(
        String player,
        int roll,
        int fromProgress,
        int toProgress,
        boolean hit,
        boolean overshoot,
        boolean won,
        String note,
        String hitVictimName,   // null if no hit
        Integer hitVictimPos    // absolute main ring pos if hit on ring
) {

    public static MoveResult gameOver() {
        return new MoveResult(
                "N/A",
                0,
                0,
                0,
                false,
                false,
                false,
                "Game over",
                null,
                null
        );
    }

    /**
     * A simple rule for "forfeit" in this implementation:
     * - you stayed on the same square AND (overshoot OR hit)
     */
    public boolean forfeited() {
        return fromProgress == toProgress && (overshoot || hit);
    }
}
