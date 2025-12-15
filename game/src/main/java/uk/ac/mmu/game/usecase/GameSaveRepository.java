package uk.ac.mmu.game.usecase;

import java.util.List;
import java.util.UUID;

public interface GameSaveRepository {

    UUID save(GameSave save) throws Exception;

    GameSave load(UUID id) throws Exception;

    /**
     * Lists all saved game IDs in storage.
     * Used by CLI for --list-saves.
     */
    List<UUID> listIds() throws Exception;

    /**
     * Returns every saved entry in storage in file order.
     * Used when the storage is append-only (games.json).
     */
    List<GameSave> listAll() throws Exception;
}
