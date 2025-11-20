package uk.ac.mmu.game.domain;

import java.util.List;
import java.util.Objects;

/**
 * Immutable Value Object representing the result of checking for a hit
 * on the main ring. It encapsulates whether a hit occurred, who was hit,
 * and at which absolute board position.
 */
public final class HitInfo {

    private static final HitInfo NO_HIT = new HitInfo(false, null, null);

    private final boolean hit;
    private final String victimName;
    private final Integer victimPosAbs; // absolute main ring position (e.g. 12)

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
     * Domain logic: given a board, a moving player, a prospective progress
     * value and all players, work out if this move would land on another
     * piece on the main ring.
     */
    public static HitInfo detect(Board board, Player mover, int candidateProgress, List<Player> allPlayers) {
        // Hits only happen on the shared main ring
        if (candidateProgress >= board.mainSize()) {
            return noHit();
        }

        int targetPos = board.mainRingPosFor(mover, candidateProgress);

        for (Player other : allPlayers) {
            if (other == mover) {
                continue;
            }
            if (other.getProgress() < board.mainSize()) {
                int otherPos = board.mainRingPosFor(other, other.getProgress());
                if (otherPos == targetPos) {
                    return HitInfo.of(other.getName(), otherPos);
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

    @Override
    public String toString() {
        return "HitInfo{" +
                "hit=" + hit +
                ", victimName='" + victimName + '\'' +
                ", victimPosAbs=" + victimPosAbs +
                '}';
    }
}
