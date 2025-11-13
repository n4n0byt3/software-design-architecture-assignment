package uk.ac.mmu.game.domain;

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
        Integer hitVictimPos    // absolute main ring pos (e.g., 12) if hit on ring
) {
    public static MoveResult gameOver() {
        return new MoveResult("N/A", 0, 0, 0, false, false, false, "Game over", null, null);
    }
}
