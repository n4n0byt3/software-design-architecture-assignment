package uk.ac.mmu.game.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void endProgressSmallBoard() {
        Board board = new Board(18, 3);
        assertEquals(20, board.endProgress()); // 18 + 3 - 1
    }

    @Test
    void labelForHomeAndMainPositionsRed() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");

        assertEquals("Home (Position 1)", board.labelFor(red, 0));
        assertEquals("Position 2", board.labelFor(red, 1));
        assertEquals("Position 10", board.labelFor(red, 9));
        assertEquals("Home (Position 1)", board.labelFor(red, 0));
    }

    @Test
    void labelForHomeAndMainPositionsBlue() {
        Board board = new Board(18, 3);
        Player blue = new Player("Blue", 10, "B");

        assertEquals("Home (Position 10)", board.labelFor(blue, 0));
        assertEquals("Position 11", board.labelFor(blue, 1));
        assertEquals("Position 17", board.labelFor(blue, 7)); // 10 -> 11..18 -> 1..4
    }

    @Test
    void labelForTailPositions() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");

        int end = board.endProgress(); // 20
        assertEquals("Tail Position R1", board.labelFor(red, 18));
        assertEquals("Tail Position R2", board.labelFor(red, 19));
        assertEquals("R3 (End)", board.labelFor(red, end));
    }

    @Test
    void mainRingPosForWrapsCorrectly() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        assertEquals(1, board.mainRingPosFor(red, 0));
        assertEquals(2, board.mainRingPosFor(red, 1));
        assertEquals(18, board.mainRingPosFor(red, 17));

        assertEquals(10, board.mainRingPosFor(blue, 0));
        assertEquals(11, board.mainRingPosFor(blue, 1));
        assertEquals(9, board.mainRingPosFor(blue, 17)); // wrap around
    }
}
