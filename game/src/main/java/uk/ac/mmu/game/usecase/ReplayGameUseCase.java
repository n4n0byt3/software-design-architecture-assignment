package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.MoveResult;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.infrastructure.ConsoleOutputAdapter;

import java.util.UUID;

public class ReplayGameUseCase {
    private final GameRepository repo;
    private final GameFactory factory;
    private final OutputPort out;

    public ReplayGameUseCase(GameRepository repo, GameFactory factory, OutputPort out) {
        this.repo = repo; this.factory = factory; this.out = out;
    }

    public void replay(UUID id) throws Exception {
        GameSave s = repo.load(id);

        Game game = factory.createFromSave(s); // uses FixedSeqShaker internally

        if (out instanceof ConsoleOutputAdapter coa) {
            coa.setBoard(game.getBoard());
        }

        out.printConfig(String.format(
                "[REPLAY %s] Board positions=%d, Tail positions=%d, Players=%d, singleDie=%s, exactEnd=%s, forfeitOnHit=%s",
                s.id, s.mainSize, s.tailSize, s.players, s.singleDie, s.exactEnd, s.forfeitOnHit
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

        String winner = game.winner().map(Player::getName).orElse("N/A");
        int totalTurns = game.getPlayers().stream().mapToInt(Player::getTurnsTaken).sum();
        out.printWinner(winner, totalTurns, game.winner().map(Player::getTurnsTaken).orElse(0));
    }
}
