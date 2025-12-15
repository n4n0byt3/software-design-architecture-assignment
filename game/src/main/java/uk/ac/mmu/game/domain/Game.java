package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Core domain object representing a single game session.
 *
 * <p>Patterns used:
 * <ul>
 *   <li>Strategy (DiceShaker, Rules)</li>
 *   <li>Decorator (ExactEndDecorator, ForfeitOnHitDecorator, RecordingDiceShaker)</li>
 *   <li>State (ReadyState, InPlayState, GameOverState)</li>
 *   <li>Observer (GameObserver interfaces)</li>
 * </ul>
 *
 * <p>Clean Architecture note:
 * this class has no dependencies on Spring, file I/O, console I/O etc.
 */
public class Game {

    private final Board board;
    private final TurnOrder turnOrder;
    private final Rules rules;
    private final DiceShaker dice;

    private final List<MoveResult> timeline = new ArrayList<>();
    private GameState state = new ReadyState();

    private final List<GameStateObserver> stateObservers = new ArrayList<>();
    private final List<PlayerTurnObserver> turnObservers = new ArrayList<>();
    private final List<GameFinishedObserver> finishedObservers = new ArrayList<>();

    public Game(Board board, List<Player> players, Rules rules, DiceShaker dice) {
        if (board == null) throw new IllegalArgumentException("board is required");
        if (players == null || players.isEmpty()) throw new IllegalArgumentException("at least one player is required");
        if (rules == null) throw new IllegalArgumentException("rules are required");
        if (dice == null) throw new IllegalArgumentException("dice is required");

        this.board = board;
        this.turnOrder = new TurnOrder(players);
        this.rules = rules;
        this.dice = dice;
    }

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

    // Package-private for tests (GameLifecycleTest).
    GameState getState() {
        return state;
    }

    public void addObserver(GameObserver observer) {
        if (observer == null) return;
        addStateObserver(observer);
        addTurnObserver(observer);
        addFinishedObserver(observer);
    }

    public void addStateObserver(GameStateObserver observer) {
        if (observer != null) stateObservers.add(observer);
    }

    public void addTurnObserver(PlayerTurnObserver observer) {
        if (observer != null) turnObservers.add(observer);
    }

    public void addFinishedObserver(GameFinishedObserver observer) {
        if (observer != null) finishedObservers.add(observer);
    }

    public void switchTo(GameState next) {
        if (next == null) throw new IllegalArgumentException("next state is required");

        String from = state.name();
        String to = next.name();

        this.state = next;
        next.enter(this);

        for (GameStateObserver obs : stateObservers) {
            obs.onStateChanged(this, from, to);
        }
    }

    public MoveResult playTurn() {
        return state.playTurn(this);
    }

    public boolean isOver() {
        return state instanceof GameOverState;
    }

    public Optional<Player> winner() {
        return turnOrder.all().stream().filter(p -> p.isAtEnd(board)).findFirst();
    }

    public List<MoveResult> timeline() {
        return List.copyOf(timeline);
    }

    public void record(MoveResult result) {
        if (result == null) throw new IllegalArgumentException("move result is required");
        timeline.add(result);
        validateInvariants();
    }

    public void notifyTurnPlayed(Player current, MoveResult result) {
        for (PlayerTurnObserver obs : turnObservers) {
            obs.onTurnPlayed(this, result, current);
        }
    }

    public void notifyGameFinished(Player winner) {
        int totalTurns = turnOrder.all().stream().mapToInt(Player::getTurnsTaken).sum();
        int winnerTurns = (winner != null) ? winner.getTurnsTaken() : 0;

        for (GameFinishedObserver obs : finishedObservers) {
            obs.onGameFinished(this, winner, totalTurns, winnerTurns);
        }
    }

    private void validateInvariants() {
        int end = board.endProgress();
        for (Player p : turnOrder.all()) {
            int prog = p.getProgress();
            if (prog < 0 || prog > end) {
                throw new IllegalStateException("Player progress out of range: " + p + " (end=" + end + ")");
            }
        }
    }
}
