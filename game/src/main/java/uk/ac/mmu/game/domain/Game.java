package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core domain object representing a single game session.
 *
 * It coordinates:
 *  - the {@link Board}
 *  - the {@link Player}s (via {@link TurnOrder})
 *  - {@link Rules} for move validation and special behaviour (Decorators)
 *  - {@link DiceShaker} strategies
 *
 * === Lifecycle State Machine (Week 7) ===
 *
 * The Game has a discrete lifecycle described by the GameStates model:
 *
 *   States:
 *     - ReadyToPlay (initial)
 *     - InPlay
 *     - GameOver (terminal)
 *
 *   Example state transitions:
 *     ReadyToPlay --turn--> InPlay
 *     InPlay      --turn--> InPlay  (while no winner)
 *     InPlay      --turn--> GameOver (when a winning move is played)
 *
 * Only one GameState is active at a time. The State pattern is used to
 * implement this lifecycle:
 *
 *   - {@link ReadyState}     => "ReadyToPlay"
 *   - {@link InPlayState}    => "InPlay"
 *   - {@link GameOverState}  => "GameOver"
 *
 * === Observer Pattern (with ISP) ===
 *
 * Game also supports observers which are notified of:
 *
 *   - State transitions ({@link GameStateObserver})
 *   - Turns being played ({@link PlayerTurnObserver})
 *   - Game completion ({@link GameFinishedObserver})
 *
 * The composite {@link GameObserver} interface combines the three; this
 * demonstrates the Interface Segregation Principle whilst still allowing
 * a single object (e.g. the console adapter) to observe everything.
 *
 * This keeps side-effects such as printing/logging outside the core domain.
 */
public class Game {

    private final Board board;
    private final TurnOrder turnOrder;
    private final Rules rules;
    private final DiceShaker dice;
    private final List<MoveResult> timeline = new ArrayList<>();

    // --- State pattern: lifecycle state (ReadyToPlay, InPlay, GameOver) ---
    private GameState state = new ReadyState();

    // --- Observer pattern (ISP: separate concerns) ---
    private final List<GameStateObserver> stateObservers = new ArrayList<>();
    private final List<PlayerTurnObserver> turnObservers = new ArrayList<>();
    private final List<GameFinishedObserver> finishedObservers = new ArrayList<>();

    public Game(Board board, List<Player> players, Rules rules, DiceShaker dice) {
        if (board == null) throw new IllegalArgumentException("board is required");
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("at least one player is required");
        }
        if (rules == null) throw new IllegalArgumentException("rules are required");
        if (dice == null) throw new IllegalArgumentException("dice are required");

        this.board = board;
        this.turnOrder = new TurnOrder(players);
        this.rules = rules;
        this.dice = dice;
    }

    // --- Accessors used by states and use case ---

    public Board getBoard() {
        return board;
    }

    public TurnOrder getTurnOrder() {
        return turnOrder;
    }

    /**
     * Returns a defensive copy so callers cannot mutate
     * the internal player list (encapsulation).
     */
    public List<Player> getPlayers() {
        return List.copyOf(turnOrder.all());
    }

    public Rules getRules() {
        return rules;
    }

    public DiceShaker getDice() {
        return dice;
    }

    GameState getState() {
        return state;
    }

    // --- Observer registration (ISP) ---

    public void addStateObserver(GameStateObserver observer) {
        if (observer != null) stateObservers.add(observer);
    }

    public void addTurnObserver(PlayerTurnObserver observer) {
        if (observer != null) turnObservers.add(observer);
    }

    public void addFinishedObserver(GameFinishedObserver observer) {
        if (observer != null) finishedObservers.add(observer);
    }

    /**
     * Convenience method: register an observer for all events.
     */
    public void addObserver(GameObserver observer) {
        if (observer == null) return;
        addStateObserver(observer);
        addTurnObserver(observer);
        addFinishedObserver(observer);
    }

    public void removeStateObserver(GameStateObserver observer) {
        stateObservers.remove(observer);
    }

    public void removeTurnObserver(PlayerTurnObserver observer) {
        turnObservers.remove(observer);
    }

    public void removeFinishedObserver(GameFinishedObserver observer) {
        finishedObservers.remove(observer);
    }

    // --- State management ---

    /**
     * Change the current game state and notify state observers
     * (e.g. ReadyToPlay -> InPlay, InPlay -> GameOver).
     */
    public void switchTo(GameState next) {
        if (next == null) throw new IllegalArgumentException("next state is required");
        String from = state.name();
        String to = next.name();
        this.state = next;
        next.enter(this);

        // Notify observers of state change
        for (GameStateObserver obs : stateObservers) {
            obs.onStateChanged(this, from, to);
        }
    }

    // --- Gameplay ---

    /**
     * Executes one logical turn by delegating to the current state.
     * ReadyState ("ReadyToPlay") will switch to InPlay on first call.
     */
    public MoveResult playTurn() {
        return state.playTurn(this);
    }

    /**
     * Game is over once we are in the terminal GameOverState.
     */
    public boolean isOver() {
        return state instanceof GameOverState;
    }

    public Optional<Player> winner() {
        return turnOrder.all().stream()
                .filter(p -> p.isAtEnd(board))
                .findFirst();
    }

    public List<MoveResult> timeline() {
        return List.copyOf(timeline);
    }

    /**
     * Called by states after a MoveResult has been produced.
     * Maintains the move history and checks invariants.
     */
    public void record(MoveResult mr) {
        if (mr == null) throw new IllegalArgumentException("move result is required");
        timeline.add(mr);
        checkInvariants();
    }

    // --- Observer notifications used by states ---

    /**
     * Notify observers that a turn has been played.
     */
    void notifyTurnPlayed(Player current, MoveResult result) {
        for (PlayerTurnObserver obs : turnObservers) {
            obs.onTurnPlayed(this, result, current);
        }
    }

    /**
     * Notify observers that the game has finished.
     */
    void notifyGameFinished(Player winner) {
        int totalTurns = turnOrder.all().stream()
                .mapToInt(Player::getTurnsTaken)
                .sum();
        int winnerTurns = (winner != null ? winner.getTurnsTaken() : 0);

        for (GameFinishedObserver obs : finishedObservers) {
            obs.onGameFinished(this, winner, totalTurns, winnerTurns);
        }
    }

    // --- Invariants ---

    private void checkInvariants() {
        int end = board.endProgress();
        for (Player p : turnOrder.all()) {
            int prog = p.getProgress();
            if (prog < 0 || prog > end) {
                throw new IllegalStateException(
                        "Player progress out of range: " + p + " (end=" + end + ")"
                );
            }
        }
    }
}
