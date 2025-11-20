package uk.ac.mmu.game.infrastructure;

import uk.ac.mmu.game.domain.*;
import uk.ac.mmu.game.usecase.OutputPort;

/**
 * Console-based presenter and observer.
 *
 * Acts as:
 *  - An {@link OutputPort} (presentation boundary).
 *  - A {@link GameObserver} (via OutputPort extending the observer interfaces).
 *
 * All printing logic is isolated here, keeping the domain model free
 * of any I/O concerns and satisfying the Ports & Adapters style.
 */
public class ConsoleOutputAdapter implements OutputPort {

    private Board board;

    @Override
    public void setBoard(Board board) {
        this.board = board;
    }

    // ----- OutputPort "printing" methods -----

    @Override
    public void printTurn(MoveResult r, int turnsForPlayer, Player p) {
        System.out.printf("%s turn %d rolls %d%n", r.player(), turnsForPlayer, r.roll());

        if (r.hit() && r.hitVictimName() != null && r.hitVictimPos() != null) {
            System.out.printf("%s Position %d hit! %n", r.hitVictimName(), r.hitVictimPos());
        }
        if (r.overshoot()) {
            System.out.printf("%s overshoots!%n", r.player());
        }

        String fromLabel = board.labelFor(p, r.fromProgress());
        String toLabel   = board.labelFor(p, r.toProgress());
        if (r.fromProgress() == r.toProgress()) {
            System.out.printf("%s remains at %s%n", r.player(), toLabel);
        } else {
            System.out.printf("%s moves from %s to %s%n", r.player(), fromLabel, toLabel);
        }
    }

    @Override
    public void printWinner(String player, int totalTurns, int winnerTurns) {
        System.out.printf("%n%s wins in %d moves!%nTotal plays %d%n", player, winnerTurns, totalTurns);
    }

    @Override
    public void printConfig(Object cfg) {
        System.out.println(cfg);
    }

    @Override
    public void printGameOver() {
        System.out.println("Game over");
    }

    @Override
    public void printState(String from, String to) {
        System.out.printf("Game state %s -> %s%n", from, to);
    }

    // ----- GameObserver callbacks (via OutputPort extends GameObserver) -----

    @Override
    public void onStateChanged(Game game, String from, String to) {
        printState(from, to);
    }

    @Override
    public void onTurnPlayed(Game game, MoveResult result, Player currentPlayer) {
        // currentPlayer.turnsTaken has already been incremented by the rules
        printTurn(result, currentPlayer.getTurnsTaken(), currentPlayer);
    }

    @Override
    public void onGameFinished(Game game, Player winner, int totalTurns, int winnerTurns) {
        String winnerName = (winner != null ? winner.getName() : "N/A");
        printWinner(winnerName, totalTurns, winnerTurns);
    }
}
