package uk.ac.mmu.game.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.mmu.game.usecase.*;
import uk.ac.mmu.game.domain.*;
import uk.ac.mmu.game.usecase.GameFacade;

@Configuration
public class SpringConfig {

    @Bean
    public OutputPort outputPort() {
        return new ConsoleOutputAdapter();
    }

    @Bean
    public GameFactory gameFactory() {
        // For the assignment we use the basic 2-player variant by default.
        // This can easily be swapped for FourPlayerGameFactory or others.
        return new BasicGameFactory();
    }

    @Bean
    public GameMediator gameMediator() {
        return new SimpleConsoleMediator();
    }

    @Bean
    public PlayGameUseCase playGameUseCase(GameFactory f, OutputPort o, GameMediator mediator) {
        return new PlayGameUseCase(f, o, mediator);
    }

    @Bean
    public GameFacade gameFacade(PlayGameUseCase playGameUseCase) {
        return new GameFacade(playGameUseCase);
    }
}
