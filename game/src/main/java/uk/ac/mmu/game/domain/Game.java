package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core domain object representing a single game session.
 *
 * Acts as the Subject in the Observer pattern (Week 4):
 * - Publishes state change events to {@link GameStateObserver}s
 * - Publishes turn events to {@link PlayerTurnObserver}s
 * - Publishes completion events to {@link GameFinishedObserver}s
 *
 * Uses:
 * - Strategy (DiceShaker, Rules)
 * - Decorator (ExactEndDecorator, ForfeitOnHitDecorator)
 * - State (ReadyState, InPlayState, GameOverState)
 * - Observer (GameObserver interfaces).
 */
public final class Game {

    private final Board board;
    private final TurnOrder turnOrder;
    private final Rules rules;
    private final DiceShaker dice;
    private final List<MoveResult> timeline = new ArrayList<>();

    // lifecycle state
    private GameState state = new ReadyState();

    // observers
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

    // Accessors

    public Board getBoard() {
        return board;
    }

    public TurnOrder getTurnOrder() {
        return turnOrder;
    }

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

    // Observer registration

    public void addStateObserver(GameStateObserver observer) {
        if (observer != null) {
            stateObservers.add(observer);
        }
    }

    public void addTurnObserver(PlayerTurnObserver observer) {
        if (observer != null) {
            turnObservers.add(observer);
        }
    }

    public void addFinishedObserver(GameFinishedObserver observer) {
        if (observer != null) {
            finishedObservers.add(observer);
        }
    }

    public void addObserver(GameObserver observer) {
        if (observer == null) {
            return;
        }
        addStateObserver(observer);
        addTurnObserver(observer);
        addFinishedObserver(observer);
    }

    // NEW: observer removal (typical Week-4 pattern)

    public void removeStateObserver(GameStateObserver observer) {
        stateObservers.remove(observer);
    }

    public void removeTurnObserver(PlayerTurnObserver observer) {
        turnObservers.remove(observer);
    }

    public void removeFinishedObserver(GameFinishedObserver observer) {
        finishedObservers.remove(observer);
    }

    public void removeObserver(GameObserver observer) {
        if (observer == null) {
            return;
        }
        removeStateObserver(observer);
        removeTurnObserver(observer);
        removeFinishedObserver(observer);
    }

    // State management

    public void switchTo(GameState next) {
        if (next == null) {
            throw new IllegalArgumentException("next state is required");
        }
        String from = state.name();
        String to = next.name();
        this.state = next;
        next.enter(this);

        for (GameStateObserver obs : stateObservers) {
            obs.onStateChanged(this, from, to);
        }
    }

    // Gameplay

    public MoveResult playTurn() {
        return state.playTurn(this);
    }

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

    public void record(MoveResult mr) {
        if (mr == null) {
            throw new IllegalArgumentException("move result is required");
        }
        timeline.add(mr);
        checkInvariants();
    }

    // Observer notifications

    public void notifyTurnPlayed(Player current, MoveResult result) {
        for (PlayerTurnObserver obs : turnObservers) {
            obs.onTurnPlayed(this, result, current);
        }
    }

    public void notifyGameFinished(Player winner) {
        int totalTurns = turnOrder.all().stream()
                .mapToInt(Player::getTurnsTaken)
                .sum();
        int winnerTurns = (winner != null) ? winner.getTurnsTaken() : 0;

        for (GameFinishedObserver obs : finishedObservers) {
            obs.onGameFinished(this, winner, totalTurns, winnerTurns);
        }
    }

    // Invariants

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
