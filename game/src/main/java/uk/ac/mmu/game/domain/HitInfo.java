package uk.ac.mmu.game.domain;

import java.util.List;
import java.util.Objects;

/**
 * Result of checking whether a move would HIT another player on the shared main ring.
 *
 * <p>Hits only occur on the main ring (tail squares are player-specific).
 */
public final class HitInfo {

    private static final HitInfo NO_HIT = new HitInfo(false, null, null);

    private final boolean hit;
    private final String victimName;
    private final Integer victimPosAbs;

    private HitInfo(boolean hit, String victimName, Integer victimPosAbs) {
        this.hit = hit;
        this.victimName = victimName;
        this.victimPosAbs = victimPosAbs;
    }

    public static HitInfo noHit() {
        return NO_HIT;
    }

    public static HitInfo of(String victimName, int victimPosAbs) {
        return new HitInfo(true, victimName, victimPosAbs);
    }

    public boolean hit() {
        return hit;
    }

    public String victimName() {
        return victimName;
    }

    public Integer victimPosAbs() {
        return victimPosAbs;
    }

    /**
     * Detects whether the mover would land on an occupied main-ring square.
     *
     * @param candidateProgress the proposed target progress (already clamped if desired by caller)
     */
    public static HitInfo detect(Board board, Player mover, int candidateProgress, List<Player> allPlayers) {
        if (candidateProgress >= board.mainSize()) {
            return noHit(); // tail is not shared
        }

        int targetAbsPos = board.mainRingPosFor(mover, candidateProgress);

        for (Player other : allPlayers) {
            if (other == mover) {
                continue;
            }

            if (other.getProgress() < board.mainSize()) {
                int otherAbsPos = board.mainRingPosFor(other, other.getProgress());
                if (otherAbsPos == targetAbsPos) {
                    return HitInfo.of(other.getName(), otherAbsPos);
                }
            }
        }

        return noHit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HitInfo hitInfo)) return false;
        return hit == hitInfo.hit
                && Objects.equals(victimName, hitInfo.victimName)
                && Objects.equals(victimPosAbs, hitInfo.victimPosAbs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hit, victimName, victimPosAbs);
    }
}
