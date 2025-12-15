package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Variation: forfeit if a HIT would occur.
 *
 * <p>If a move would land on another player on the main ring,
 * the mover stays in place and the turn is forfeited.
 */
public class ForfeitOnHitDecorator implements Rules {

    private final Rules inner;

    public ForfeitOnHitDecorator(Rules inner) {
        if (inner == null) {
            throw new IllegalArgumentException("inner rules are required");
        }
        this.inner = inner;
    }

    @Override
    public MoveResult apply(Board board, Player player, int roll, List<Player> allPlayers) {
        int end = board.endProgress();

        int from = player.getProgress();
        int proposedTo = Math.min(from + roll, end);

        HitInfo hitInfo = HitInfo.detect(board, player, proposedTo, allPlayers);

        if (hitInfo.hit()) {
            // Forfeit on hit: stay where you are.
            return new MoveResult(
                    player.getName(),
                    roll,
                    from,
                    from,
                    true,
                    false,
                    false,
                    "",
                    hitInfo.victimName(),
                    hitInfo.victimPosAbs()
            );
        }

        return inner.apply(board, player, roll, allPlayers);
    }
}
