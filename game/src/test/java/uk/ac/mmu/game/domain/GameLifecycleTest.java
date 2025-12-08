package uk.ac.mmu.game.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLifecycleTest {

    @Test
    void initialStateIsReadyToPlayAndFirstTurnTransitionsToInPlay() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        Game game = new Game(board, List.of(red, blue), new BasicRules(), new FixedSeqShaker(1, 1));

        assertEquals("ReadyToPlay", game.getState().name());

        MoveResult first = game.playTurn();
        assertNotNull(first);
        assertEquals("InPlay", game.getState().name());
        assertFalse(game.isOver());
        assertEquals(1, game.timeline().size());
    }

    @Test
    void gameReachesGameOverAndWinnerIsBlueInExampleScenario() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        // Basic game example: dice {12,12,7,8} – Blue wins
        Game game = new Game(board, List.of(red, blue),
                new BasicRules(), new FixedSeqShaker(12, 12, 7, 8));

        while (!game.isOver()) {
            game.playTurn();
        }

        assertTrue(game.isOver());
        assertEquals("GameOver", game.getState().name());
        assertTrue(game.winner().isPresent());
        assertEquals("Blue", game.winner().get().getName());
    }

    @Test
    void extraTurnsInGameOverReturnGameOverSentinel() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        Game game = new Game(board, List.of(red, blue),
                new BasicRules(), new FixedSeqShaker(12, 12, 7, 8, 12, 12));

        while (!game.isOver()) {
            game.playTurn();
        }

        MoveResult extra1 = game.playTurn();
        MoveResult extra2 = game.playTurn();

        assertEquals("Game over", extra1.note());
        assertEquals("Game over", extra2.note());
        assertTrue(game.isOver());
    }
}
