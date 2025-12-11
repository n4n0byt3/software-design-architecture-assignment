package uk.ac.mmu.game.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicRulesTest {

    @Test
    void overshootMovesToEndAndWinsInBasicRules() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        red.setProgress(19); // one before end (end=20)

        BasicRules rules = new BasicRules();
        MoveResult mr = rules.apply(board, red, 5, List.of(red));

        assertEquals(19, mr.fromProgress());
        assertEquals(board.endProgress(), mr.toProgress());
        assertTrue(mr.overshoot());
        assertTrue(mr.won());
        assertEquals(board.endProgress(), red.getProgress());

        // Turn counting is handled by Game/InPlayState, not by BasicRules.
        // So rules must NOT increment turnsTaken.
        assertEquals(0, red.getTurnsTaken());
    }

    @Test
    void hitIsReportedButMoveStillHappensWithBasicRules() {
        Board board = new Board(18, 3);

        Player p1 = new Player("P1", 1, "R");
        Player p2 = new Player("P2", 1, "B");

        // p2 at progress 3 -> absolute main pos = 4
        p2.setProgress(3);

        BasicRules rules = new BasicRules();
        MoveResult mr = rules.apply(board, p1, 3, List.of(p1, p2));

        assertTrue(mr.hit(), "Basic rules should still allow landing on an occupied square");
        assertEquals(3, mr.toProgress());
        assertEquals(3, p1.getProgress());
        assertEquals("P2", mr.hitVictimName());
        assertEquals(board.mainRingPosFor(p2, p2.getProgress()), mr.hitVictimPos());
    }
}
