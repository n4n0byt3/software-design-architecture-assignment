package uk.ac.mmu.game.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/** Stores game snapshots under ~/.simple-frustration/<uuid>.json */
public class FileGameRepository implements GameRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path root = Path.of(System.getProperty("user.home"), ".simple-frustration");

    public FileGameRepository() {
        try { Files.createDirectories(root); } catch (Exception ignored) {}
    }

    @Override
    public UUID save(GameSave save) throws Exception {
        if (save.id == null) save.id = UUID.randomUUID();
        File f = root.resolve(save.id.toString() + ".json").toFile();
        mapper.writerWithDefaultPrettyPrinter().writeValue(f, save);
        return save.id;
    }

    @Override
    public GameSave load(UUID id) throws Exception {
        File f = root.resolve(id.toString() + ".json").toFile();
        if (!f.exists()) throw new IllegalArgumentException("Save not found: " + f.getAbsolutePath());
        return mapper.readValue(f, GameSave.class);
    }
}
