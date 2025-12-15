package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Variation: player must land exactly on END to win.
 *
 * <p>If a roll would overshoot END, the player forfeits and remains in place.
 */
public class ExactEndDecorator implements Rules {

    private final Rules inner;

    public ExactEndDecorator(Rules inner) {
        if (inner == null) {
            throw new IllegalArgumentException("inner rules are required");
        }
        this.inner = inner;
    }

    @Override
    public MoveResult apply(Board board, Player player, int roll, List<Player> allPlayers) {
        int end = board.endProgress();

        int from = player.getProgress();
        int proposedTo = from + roll;

        if (proposedTo > end) {
            // Forfeit on overshoot: stay where you are.
            return new MoveResult(
                    player.getName(),
                    roll,
                    from,
                    from,
                    false,
                    true,
                    false,
                    "",
                    null,
                    null
            );
        }

        return inner.apply(board, player, roll, allPlayers);
    }
}
