package uk.ac.mmu.game.infrastructure;

import uk.ac.mmu.game.domain.Board;
import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.usecase.GameOutputPort;

/**
 * Console presenter + domain observer.
 *
 * <p>Keeps all I/O in infrastructure (Ports & Adapters / Clean Architecture).
 * Domain and usecase layers remain framework-agnostic and testable.
 */
public class ConsoleOutputAdapter implements GameOutputPort {

    private Board board;

    @Override
    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public void printTurn(MoveResult r, int turnsForPlayer, Player playerCtx) {
        System.out.printf("%s turn %d rolls %d%n", r.player(), turnsForPlayer, r.roll());

        if (r.hit() && r.hitVictimName() != null && r.hitVictimPos() != null) {
            System.out.printf("%s Position %d hit!%n", r.hitVictimName(), r.hitVictimPos());
        }

        if (r.overshoot()) {
            System.out.printf("%s overshoots!%n", r.player());
        }

        String fromLabel = board.labelFor(playerCtx, r.fromProgress());
        String toLabel = board.labelFor(playerCtx, r.toProgress());

        if (r.fromProgress() == r.toProgress()) {
            System.out.printf("%s remains at %s%n", r.player(), toLabel);
        } else {
            System.out.printf("%s moves from %s to %s%n", r.player(), fromLabel, toLabel);
        }
    }

    @Override
    public void printWinner(String playerName, int totalTurns, int winnerTurns) {
        System.out.printf("%n%s wins in %d turns!%nTotal turns %d%n", playerName, winnerTurns, totalTurns);
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

    // Observer callbacks

    @Override
    public void onStateChanged(Game game, String from, String to) {
        printState(from, to);
    }

    @Override
    public void onTurnPlayed(Game game, MoveResult result, Player currentPlayer) {
        printTurn(result, currentPlayer.getTurnsTaken(), currentPlayer);
    }

    @Override
    public void onGameFinished(Game game, Player winner, int totalTurns, int winnerTurns) {
        printWinner(winner != null ? winner.getName() : "N/A", totalTurns, winnerTurns);
    }
}
