package uk.ac.mmu.game.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.mmu.game.usecase.PlayGameUseCase;

@Component
public class CliRunner implements CommandLineRunner {
    private final PlayGameUseCase play;
    public CliRunner(PlayGameUseCase play) { this.play = play; }

    @Override
    public void run(String... args) {
        boolean singleDie = !hasArg(args, "--double");
        boolean exactEnd = hasArg(args, "--exact-end");
        boolean forfeitOnHit = hasArg(args, "--forfeit-on-hit");
        play.execute(singleDie, exactEnd, forfeitOnHit);
    }

    private boolean hasArg(String[] args, String key) {
        for (String a : args) if (a.equalsIgnoreCase(key)) return true;
        return false;
    }
}
