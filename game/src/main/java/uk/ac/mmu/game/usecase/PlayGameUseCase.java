package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.infrastructure.ConsoleOutputAdapter;

public class PlayGameUseCase {
    private final GameFactory factory;
    private final OutputPort out;

    public PlayGameUseCase(GameFactory factory, OutputPort out) {
        this.factory = factory;
        this.out = out;
    }

    public void execute(boolean singleDie, boolean exactEnd, boolean forfeitOnHit) {
        Game game = factory.create2P(singleDie, exactEnd, forfeitOnHit);

        if (out instanceof ConsoleOutputAdapter coa) {
            coa.setBoard(game.getBoard());
        }

        out.printConfig(String.format(
                "Board positions=%d, Tail positions=%d, Players=%s, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                game.getBoard().mainSize(),
                game.getBoard().tailSize(),
                game.getPlayers().stream().map(Player::getName).toList(),
                singleDie, exactEnd, forfeitOnHit
        ));

        while (!game.isOver()) {
            // play a turn (Ready will transition to InPlay inside)
            Player current = game.getTurnOrder().current();
            MoveResult mr = game.playTurn();

            // print any state transition that happened because of this call
            String[] t = game.drainTransition();
            if (t != null) out.printState(t[0], t[1]);

            if ("Game over".equals(mr.note())) {
                out.printGameOver();
                break;
            }
            out.printTurn(mr, current.getTurnsTaken(), current);

            // If InPlay -> GameOver happened after this turn, print that transition too
            t = game.drainTransition();
            if (t != null) out.printState(t[0], t[1]);
        }

        Player winPlayer = game.winner().orElse(null);
        String winnerName = (winPlayer != null ? winPlayer.getName() : "N/A");
        int winnerTurns = (winPlayer != null ? winPlayer.getTurnsTaken() : 0);
        int totalTurns = game.getPlayers().stream().mapToInt(Player::getTurnsTaken).sum();
        out.printWinner(winnerName, totalTurns, winnerTurns);
    }
}
