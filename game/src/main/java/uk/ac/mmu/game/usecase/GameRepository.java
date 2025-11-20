package uk.ac.mmu.game.usecase;

import java.util.UUID;

public interface GameRepository {
    UUID save(GameSave save) throws Exception;
    GameSave load(UUID id) throws Exception;
}
