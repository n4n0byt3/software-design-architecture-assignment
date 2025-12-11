package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Basic game rules:
 * - Overshoot moves the player to END and still wins.
 * - HITs are allowed (multiple players may share a square) unless
 *   ForfeitOnHitDecorator is applied.
 *
 * This class is deliberately responsible only for
 *   "given a roll, where does the piece end up?"
 * Turn counting is handled by the Game / GameState layer.
 */
public final class BasicRules implements Rules {

    @Override
    public MoveResult apply(Board board, Player p, int roll, List<Player> all) {
        int end = board.endProgress();
        int from = p.getProgress();
        int to = from + roll;
        boolean overshoot = to > end;

        if (overshoot) {
            to = end;
        }

        HitInfo hitInfo = HitInfo.detect(board, p, to, all);

        // Movement only – turns are incremented in InPlayState.
        p.setProgress(to);
        boolean won = (to == end);

        return new MoveResult(
                p.getName(),
                roll,
                from,
                to,
                hitInfo.hit(),
                overshoot,
                won,
                "",
                hitInfo.victimName(),
                hitInfo.victimPosAbs()
        );
    }
}
