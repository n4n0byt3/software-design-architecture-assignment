package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Decorator that enforces "turn is forfeit if a HIT would occur":
 * if a move would land on another player, the moving player stays in place.
 */
public final class ForfeitOnHitDecorator implements Rules {

    private final Rules inner;

    public ForfeitOnHitDecorator(Rules inner) {
        this.inner = inner;
    }

    @Override
    public MoveResult apply(Board board, Player p, int roll, List<Player> all) {
        int from = p.getProgress();
        int end = board.endProgress();
        int candidateTo = Math.min(from + roll, end);

        HitInfo hitInfo = HitInfo.detect(board, p, candidateTo, all);

        if (hitInfo.hit()) {
            // Forfeit on hit: remain at 'from', but report hit details.
            return new MoveResult(
                    p.getName(),
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

        return inner.apply(board, p, roll, all);
    }
}
