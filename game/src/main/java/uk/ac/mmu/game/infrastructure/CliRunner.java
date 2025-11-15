package uk.ac.mmu.game.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.mmu.game.usecase.GameFacade;

@Component
public class CliRunner implements CommandLineRunner {

    private final GameFacade facade;

    public CliRunner(GameFacade facade) {
        this.facade = facade;
    }

    @Override
    public void run(String... args) {
        boolean singleDie = !hasArg(args, "--double");
        boolean exactEnd = hasArg(args, "--exact-end");
        boolean forfeitOnHit = hasArg(args, "--forfeit-on-hit");

        // Client depends only on ONE object now (Façade)
        facade.runGame(singleDie, exactEnd, forfeitOnHit);
    }

    private boolean hasArg(String[] args, String key){
        for (String a: args) if (a.equalsIgnoreCase(key)) return true;
        return false;
    }
}
