package uk.ac.mmu.game.domain;

/**
 * Strategy for generating dice rolls.
 *
 * Different implementations model different behaviours:
 * - {@link RandomSingleDiceShaker}: a single 6-sided die (1–6)
 * - {@link RandomDoubleDiceShaker}: two dice summed (2–12)
 * - {@link FixedSeqShaker}: fixed sequence (for tests / replay)
 * - {@link RecordingDiceShaker}: wraps another DiceShaker and records rolls
 *
 * This follows the Strategy pattern discussed in Week 3:
 * the Game depends only on this abstraction and can swap in any
 * concrete implementation without changing its own code.
 */
public interface DiceShaker {
    int shake();
}
