package uk.ac.mmu.game.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.mmu.game.usecase.*;

/**
 * Spring DI configuration (wiring).
 *
 * <p>Clean Architecture note:
 * infrastructure owns wiring and concrete adapters, but depends only on domain/usecase.
 */
@Configuration
public class AppConfig {

    @Bean
    public GameOutputPort outputPort() {
        return new ConsoleOutputAdapter();
    }

    @Bean
    public GameFactory gameFactory() {
        return new GameFactory();
    }

    @Bean
    public GameSaveRepository gameSaveRepository() {
        return new JsonLinesGameSaveRepository();
    }

    @Bean
    public GameEventMediator gameEventMediator() {
        return new ConsoleGameEventMediator();
    }

    @Bean
    public PlayGameUseCase playGameUseCase(GameFactory factory,
                                           GameOutputPort output,
                                           GameSaveRepository repository,
                                           GameEventMediator mediator) {
        return new PlayGameUseCase(factory, output, repository, mediator);
    }

    @Bean
    public ReplayGameUseCase replayGameUseCase(GameSaveRepository repository,
                                               GameFactory factory,
                                               GameOutputPort output,
                                               GameEventMediator mediator) {
        return new ReplayGameUseCase(repository, factory, output, mediator);
    }
}
