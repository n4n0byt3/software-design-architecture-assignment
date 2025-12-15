package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Basic game rules:
 * <ul>
 *   <li>Overshoot moves the player to END and still wins.</li>
 *   <li>HITs are allowed (players may share a square) unless ForfeitOnHitDecorator is applied.</li>
 * </ul>
 *
 * <p>NOTE: This class does NOT increment turnsTaken.
 * Turn counting is handled by the game loop (InPlayState) so forfeits can skip counting.
 */
public class BasicRules implements Rules {

    @Override
    public MoveResult apply(Board board, Player player, int roll, List<Player> allPlayers) {
        int end = board.endProgress();

        int from = player.getProgress();
        int proposedTo = from + roll;

        boolean overshoot = proposedTo > end;
        int to = overshoot ? end : proposedTo;

        HitInfo hitInfo = HitInfo.detect(board, player, to, allPlayers);

        // In basic rules, move always happens (even if it hits).
        player.setProgress(to);
        boolean won = (to == end);

        return new MoveResult(
                player.getName(),
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
