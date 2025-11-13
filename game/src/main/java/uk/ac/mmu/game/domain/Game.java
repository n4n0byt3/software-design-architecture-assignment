package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {
    private final Board board;
    private final TurnOrder turnOrder;
    private final Rules rules;
    private final DiceShaker dice;
    private final List<MoveResult> timeline = new ArrayList<>();

    // --- State pattern ---
    private GameState state = new ReadyState();

    // Pending transition to be printed by the use case
    private String pendingFrom;
    private String pendingTo;

    public Game(Board board, List<Player> players, Rules rules, DiceShaker dice) {
        this.board = board;
        this.turnOrder = new TurnOrder(players);
        this.rules = rules;
        this.dice = dice;
        // ReadyState is initial; no enter() call to avoid printing until first play
    }

    // --- Accessors used by states and use case ---
    public Board getBoard() { return board; }
    public TurnOrder getTurnOrder() { return turnOrder; }
    public List<Player> getPlayers() { return turnOrder.all(); }
    public Rules getRules() { return rules; }
    public DiceShaker getDice() { return dice; }
    public GameState getState() { return state; }

    /** States call this to change phase; records a transition for the UI to print. */
    public void switchTo(GameState next) {
        String from = state.name();
        String to = next.name();
        this.state = next;
        // store a single pending transition; the use case will "drain" it
        this.pendingFrom = from;
        this.pendingTo = to;
        next.enter(this);
    }

    /** Use-case polls this after each play to print any transition. */
    public String[] drainTransition() {
        if (pendingFrom == null) return null;
        String[] t = new String[] { pendingFrom, pendingTo };
        pendingFrom = null;
        pendingTo = null;
        return t;
    }

    /** One logical turn, delegated to the current state. */
    public MoveResult playTurn() {
        return state.playTurn(this);
    }

    /** True once we’re in the terminal state. */
    public boolean isOver() { return state instanceof GameOverState; }

    public Optional<Player> winner() {
        return turnOrder.all().stream().filter(p -> p.isAtEnd(board)).findFirst();
    }

    public List<MoveResult> timeline() { return List.copyOf(timeline); }

    public void record(MoveResult mr) { timeline.add(mr); }
}
