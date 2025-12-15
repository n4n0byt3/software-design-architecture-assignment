package uk.ac.mmu.game.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.mmu.game.usecase.GameSave;
import uk.ac.mmu.game.usecase.GameSaveRepository;
import uk.ac.mmu.game.usecase.PlayGameUseCase;
import uk.ac.mmu.game.usecase.ReplayGameUseCase;

import java.util.List;
import java.util.UUID;

/**
 * Console entry point.
 * Parses args and dispatches to the appropriate use case.
 */
@Component
public class CommandLineGameRunner implements CommandLineRunner {

    private final PlayGameUseCase play;
    private final ReplayGameUseCase replay;
    private final GameSaveRepository repository;

    public CommandLineGameRunner(PlayGameUseCase play,
                                 ReplayGameUseCase replay,
                                 GameSaveRepository repository) {
        this.play = play;
        this.replay = replay;
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        try {
            if (hasArg(args, "--list-saves")) {
                listSavesWithSummary();
                return;
            }

            String replayId = getArgValue(args, "--replay");
            if (replayId != null) {
                replay.replay(UUID.fromString(replayId));
                return;
            }

            boolean largeBoard = hasArg(args, "--large-board");
            int mainSize = largeBoard ? 36 : 18;
            int tailSize = largeBoard ? 6 : 3;

            int players = 2;
            String playersVal = getArgValue(args, "--players");
            if (playersVal != null) {
                players = Integer.parseInt(playersVal);
            }
            // Per spec: large board implies 4 players minimum.
            if (largeBoard && players < 4) {
                players = 4;
            }

            boolean singleDie = hasArg(args, "--single");
            boolean exactEnd = hasArg(args, "--exact-end");
            boolean forfeitOnHit = hasArg(args, "--forfeit-on-hit");

            play.execute(mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit);

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private void listSavesWithSummary() throws Exception {
        List<GameSave> saves = repository.listAll();

        if (saves.isEmpty()) {
            System.out.println("No saved games found.");
            return;
        }

        System.out.println("Saved games (all entries):");
        for (int i = 0; i < saves.size(); i++) {
            GameSave s = saves.get(i);
            System.out.printf(
                    "%d) %s | board=%d+%d | players=%d | singleDie=%s | exactEnd=%s | forfeitOnHit=%s | rolls=%d%n",
                    i + 1,
                    s.id,
                    s.mainSize, s.tailSize,
                    s.players,
                    s.singleDie,
                    s.exactEnd,
                    s.forfeitOnHit,
                    (s.rolls == null ? 0 : s.rolls.size())
            );
        }
    }

    private static boolean hasArg(String[] args, String key) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    /**
     * Reads values in the form: --players=4, --replay=<uuid>, etc.
     */
    private static String getArgValue(String[] args, String keyEq) {
        for (String arg : args) {
            if (arg.startsWith(keyEq + "=")) {
                return arg.substring((keyEq + "=").length());
            }
        }
        return null;
    }
}
