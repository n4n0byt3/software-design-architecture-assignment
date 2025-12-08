package uk.ac.mmu.game.usecase;

import org.junit.jupiter.api.Test;
import uk.ac.mmu.game.domain.Game;
import uk.ac.mmu.game.domain.Player;
import uk.ac.mmu.game.domain.RecordingDiceShaker;

import static org.junit.jupiter.api.Assertions.*;

class GameFactoryTest {

    @Test
    void create2PSmallBoardUsesRecordingDiceAndTwoPlayers() {
        GameFactory factory = new GameFactory();
        Game game = factory.create2P(true, false, false);

        assertEquals(2, game.getPlayers().size());
        assertTrue(game.getDice() instanceof RecordingDiceShaker);
    }

    @Test
    void createLargeBoardWith4PlayersHasCorrectPlayerCount() {
        GameFactory factory = new GameFactory();
        Game game = factory.createGame(36, 6, 4, true, false, false);

        assertEquals(4, game.getPlayers().size());
        assertEquals("Red", game.getPlayers().get(0).getName());
        assertEquals("Yellow", game.getPlayers().get(3).getName());
    }

    @Test
    void createFromSaveRestoresBoardAndPlayersConfiguration() {
        GameSave save = new GameSave(
                null,
                36,
                6,
                4,
                true,
                true,
                true,
                java.util.List.of(7, 3, 8)
        );

        GameFactory factory = new GameFactory();
        Game game = factory.createFromSave(save);

        assertEquals(4, game.getPlayers().size());
        assertEquals("Red", game.getPlayers().get(0).getName());
        assertEquals("Yellow", game.getPlayers().get(3).getName());
    }
}
