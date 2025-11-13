package uk.ac.mmu.game.usecase;

import java.util.UUID;

public interface GameRepository {
    default UUID save(Object snapshot) { return UUID.randomUUID(); }
    default Object load(UUID id) { return null; }
}
