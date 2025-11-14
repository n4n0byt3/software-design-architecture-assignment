package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core domain object representing a single game session.
 * It coordinates players, board, rules and dice and delegates
 * turn logic to a GameState implementation (Ready/InPlay/GameOver).
 */
public class Game {

    private final Board board;
    private final TurnOrder turnOrder;
    private final Rules rules;
    private final DiceShaker dice;
    private final List<MoveResult> timeline = new ArrayList<>();

    // --- State pattern ---
    private GameState state = new ReadyState();

    // Pending transition (for printing "Game state X -> Y")
    private String pendingFrom;
    private String pendingTo;

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

    // --- State management ---

    /**
     * Change the current game state and remember the transition
     * so the UI can print it (Ready -> InPlay, InPlay -> GameOver).
     */
    public void switchTo(GameState next) {
        if (next == null) throw new IllegalArgumentException("next state is required");
        String from = state.name();
        String to = next.name();
        this.state = next;
        this.pendingFrom = from;
        this.pendingTo = to;
        next.enter(this);
    }

    /**
     * Called by the use case after each turn to retrieve (and clear)
     * any state transition that happened.
     *
     * @return [from, to] or null if no transition pending.
     */
    public String[] drainTransition() {
        if (pendingFrom == null) return null;
        String[] t = new String[]{pendingFrom, pendingTo};
        pendingFrom = null;
        pendingTo = null;
        return t;
    }

    // --- Gameplay ---

    /**
     * Executes one logical turn by delegating to the current state.
     * ReadyState will switch to InPlay on first call.
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
