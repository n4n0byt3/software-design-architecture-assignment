package uk.ac.mmu.game.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.mmu.game.usecase.*;

@Configuration
public class SpringConfig {
    @Bean public OutputPort outputPort() { return new ConsoleOutputAdapter(); }
    @Bean public GameFactory gameFactory() { return new GameFactory(); }
    @Bean public GameRepository gameRepository() { return new FileGameRepository(); }

    @Bean
    public PlayGameUseCase playGameUseCase(GameFactory f, OutputPort o, GameRepository r) {
        return new PlayGameUseCase(f, o, r);
    }

    @Bean
    public ReplayGameUseCase replayGameUseCase(GameRepository r, GameFactory f, OutputPort o) {
        return new ReplayGameUseCase(r, f, o);
    }
}
