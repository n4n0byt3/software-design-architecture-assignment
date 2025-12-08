package uk.ac.mmu.game.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HitInfoTest {

    @Test
    void noHitWhenNoOtherPlayersOnTarget() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        red.setProgress(0);
        blue.setProgress(5);

        HitInfo info = HitInfo.detect(board, red, 3, List.of(red, blue));
        assertFalse(info.hit());
        assertNull(info.victimName());
        assertNull(info.victimPosAbs());
    }

    @Test
    void detectsHitOnMainRing() {
        Board board = new Board(18, 3);
        // Same home index to simplify absolute position logic for testing
        Player p1 = new Player("P1", 1, "R");
        Player p2 = new Player("P2", 1, "B");

        p2.setProgress(3);              // absolute main position = 4
        HitInfo info = HitInfo.detect(board, p1, 3, List.of(p1, p2));

        assertTrue(info.hit());
        assertEquals("P2", info.victimName());
        assertEquals(board.mainRingPosFor(p2, 3), info.victimPosAbs());
    }

    @Test
    void noHitWhenCandidateIsInTail() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        Player blue = new Player("Blue", 10, "B");

        red.setProgress(board.mainSize());      // first tail position
        blue.setProgress(5);                    // on ring

        HitInfo info = HitInfo.detect(board, red, board.mainSize(), List.of(red, blue));
        assertFalse(info.hit(), "Hits should not happen on tail positions");
    }
}
