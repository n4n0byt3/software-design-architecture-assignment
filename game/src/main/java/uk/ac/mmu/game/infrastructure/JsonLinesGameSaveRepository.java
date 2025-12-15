package uk.ac.mmu.game.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.mmu.game.usecase.GameSave;
import uk.ac.mmu.game.usecase.GameSaveRepository;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stores all saved games in one append-only file:
 * <pre>
 *   &lt;project&gt;/target/saves/games.json
 * </pre>
 *
 * <p>Format: JSON Lines (NDJSON) => one {@link GameSave} per line.</p>
 *
 * <p>Advantages:
 * <ul>
 *   <li>Append-only (no rewrite required)</li>
 *   <li>Simple replay & listing</li>
 *   <li>Human-readable</li>
 * </ul>
 */
public class JsonLinesGameSaveRepository implements GameSaveRepository {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Path savesFile;

    public JsonLinesGameSaveRepository() {
        Path savesDir = Paths.get(System.getProperty("user.dir"), "target", "saves");
        this.savesFile = savesDir.resolve("games.json");

        try {
            Files.createDirectories(savesDir);
            if (!Files.exists(savesFile)) {
                Files.createFile(savesFile);
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialise saves directory/file: " + savesFile.toAbsolutePath(), e
            );
        }
    }

    @Override
    public UUID save(GameSave save) throws Exception {
        if (save == null) throw new IllegalArgumentException("save is required");

        if (save.id == null) {
            save.id = UUID.randomUUID();
        }

        String jsonLine = mapper.writeValueAsString(save) + System.lineSeparator();

        Files.writeString(
                savesFile,
                jsonLine,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        return save.id;
    }

    @Override
    public GameSave load(UUID id) throws Exception {
        if (id == null) throw new IllegalArgumentException("id is required");

        try (BufferedReader reader = Files.newBufferedReader(savesFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                GameSave candidate = mapper.readValue(line, GameSave.class);
                if (id.equals(candidate.id)) {
                    return candidate;
                }
            }
        }

        throw new IllegalArgumentException("Game id not found: " + id);
    }

    @Override
    public List<UUID> listIds() throws Exception {
        List<UUID> ids = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(savesFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                try {
                    GameSave candidate = mapper.readValue(line, GameSave.class);
                    if (candidate.id != null) {
                        ids.add(candidate.id);
                    }
                } catch (Exception ignored) {
                    // Skip corrupted lines safely
                }
            }
        }

        return ids;
    }

    @Override
    public List<GameSave> listAll() throws Exception {
        List<GameSave> saves = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(savesFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                try {
                    saves.add(mapper.readValue(line, GameSave.class));
                } catch (Exception ignored) {
                    // Skip malformed entries without killing the app
                }
            }
        }

        return saves;
    }

    /**
     * Helpful for debugging / README.
     */
    public Path savesFilePath() {
        return savesFile;
    }
}
