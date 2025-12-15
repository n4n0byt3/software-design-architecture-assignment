package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Strategy interface for game rules.
 *
 * <p>Important: rules do NOT increment turn counters.
 * Turn counting happens in the game loop (InPlayState) so forfeits can skip counting.
 */
public interface Rules {
    MoveResult apply(Board board, Player current, int roll, List<Player> allPlayers);
}
