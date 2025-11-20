package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.domain.RecordingDiceShaker;
import uk.ac.mmu.game.infrastructure.ConsoleOutputAdapter;

import java.util.UUID;

public class PlayGameUseCase {
    private final GameFactory factory;
    private final OutputPort out;
    private final GameRepository repo;

    public PlayGameUseCase(GameFactory factory, OutputPort out, GameRepository repo) {
        this.factory = factory;
        this.out = out;
        this.repo = repo;
    }

    /**
     * Run a game then save it.
     * @param mainSize  18 or 36
     * @param tailSize  3 or 6
     * @param players   2 or 4
     */
    public void execute(int mainSize, int tailSize, int players,
                        boolean singleDie, boolean exactEnd, boolean forfeitOnHit) throws Exception {

        Game game = factory.createGame(mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit);

        if (out instanceof ConsoleOutputAdapter coa) {
            coa.setBoard(game.getBoard());
        }

        out.printConfig(String.format(
                "Board positions=%d, Tail positions=%d, Players=%d, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit
        ));

        while (!game.isOver()) {
            Player current = game.getTurnOrder().current();
            MoveResult mr = game.playTurn();

            String[] t = game.drainTransition();
            if (t != null) out.printState(t[0], t[1]);

            if ("Game over".equals(mr.note())) { out.printGameOver(); break; }
            out.printTurn(mr, current.getTurnsTaken(), current);

            t = game.drainTransition();
            if (t != null) out.printState(t[0], t[1]);
        }

        Player winPlayer = game.winner().orElse(null);
        String winnerName = (winPlayer != null ? winPlayer.getName() : "N/A");
        int winnerTurns = (winPlayer != null ? winPlayer.getTurnsTaken() : 0);
        int totalTurns = game.getPlayers().stream().mapToInt(Player::getTurnsTaken).sum();
        out.printWinner(winnerName, totalTurns, winnerTurns);

        // --- Save snapshot using recorded dice sequence ---
        RecordingDiceShaker rec = (game.getDice() instanceof RecordingDiceShaker r) ? r : null;
        if (rec != null) {
            GameSave save = new GameSave(
                    null, mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit, rec.getRolls()
            );
            UUID id = repo.save(save);
            out.printConfig("Saved game id: " + id);
        } else {
            out.printConfig("Note: dice were not recordable; game not saved.");
        }
    }
}
