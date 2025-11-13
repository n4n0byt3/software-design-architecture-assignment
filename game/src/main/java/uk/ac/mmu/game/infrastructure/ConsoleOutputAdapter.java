package uk.ac.mmu.game.infrastructure;

import uk.ac.mmu.game.domain.*;
import uk.ac.mmu.game.usecase.OutputPort;

public class ConsoleOutputAdapter implements OutputPort {
    private Board board;
    public void setBoard(Board board) { this.board = board; }

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
        System.out.printf("%n%s wins in %d turns!%nTotal turns %d%n", player, winnerTurns, totalTurns);
    }

    @Override
    public void printConfig(Object cfg) { System.out.println(cfg); }

    @Override
    public void printGameOver() { System.out.println("Game over"); }

    @Override
    public void printState(String from, String to) {
        System.out.printf("Game state %s -> %s%n", from, to);
    }
}
