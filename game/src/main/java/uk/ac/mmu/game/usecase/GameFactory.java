package uk.ac.mmu.game.usecase;

import uk.ac.mmu.game.domain.Game;

public interface GameFactory {
    Game createGame(boolean singleDie, boolean exactEnd, boolean forfeitOnHit);
}
