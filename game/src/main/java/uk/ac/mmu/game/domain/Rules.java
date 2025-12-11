package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Strategy for applying movement rules to a single turn.
 *
 * Implementations encapsulate different rule sets:
 * - {@link BasicRules}: default Frustration rules (overshoot allowed, hits permitted)
 * - {@link ExactEndDecorator}: enforces "must land exactly on END"
 * - {@link ForfeitOnHitDecorator}: enforces "turn is forfeit if HIT would occur"
 *
 * This is the core of the Variations requirement:
 * each combination of decorators represents a configured Rules strategy
 * without changing the Game or Player classes (Open/Closed).
 */
public interface Rules {

    /**
     * Apply the movement rules for one turn.
     *
     * @param board       board configuration
     * @param current     the player taking the turn
     * @param roll        dice roll for this turn
     * @param allPlayers  all players (needed for HIT detection)
     * @return immutable MoveResult capturing the outcome
     */
    MoveResult apply(Board board, Player current, int roll, List<Player> allPlayers);
}
