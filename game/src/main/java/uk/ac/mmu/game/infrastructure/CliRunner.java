package uk.ac.mmu.game.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.mmu.game.usecase.PlayGameUseCase;
import uk.ac.mmu.game.usecase.ReplayGameUseCase;

import java.util.UUID;

@Component
public class CliRunner implements CommandLineRunner {
    private final PlayGameUseCase play;
    private final ReplayGameUseCase replay;

    public CliRunner(PlayGameUseCase play, ReplayGameUseCase replay) {
        this.play = play; this.replay = replay;
    }

    @Override
    public void run(String... args) {
        try {
            // Replay mode?
            String replayId = getArgValue(args, "--replay");
            if (replayId != null) {
                replay.replay(UUID.fromString(replayId));
                return;
            }

            boolean large = hasArg(args, "--large-board");
            int mainSize = large ? 36 : 18;
            int tailSize = large ? 6  : 3;

            int players = 2;
            String playersVal = getArgValue(args, "--players");
            if (playersVal != null) players = Integer.parseInt(playersVal);
            if (large && players < 4) players = 4; // spec large board is 4 players

            boolean singleDie = !hasArg(args, "--double");
            boolean exactEnd = hasArg(args, "--exact-end");
            boolean forfeitOnHit = hasArg(args, "--forfeit-on-hit");

            play.execute(mainSize, tailSize, players, singleDie, exactEnd, forfeitOnHit);

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    private static boolean hasArg(String[] args, String key) {
        for (String a : args) if (a.equalsIgnoreCase(key)) return true;
        return false;
    }
    private static String getArgValue(String[] args, String keyEq) {
        for (String a : args) {
            if (a.startsWith(keyEq + "=")) return a.substring((keyEq + "=").length());
        }
        return null;
    }
}
