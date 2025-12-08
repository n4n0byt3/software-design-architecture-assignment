package uk.ac.mmu.game.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForfeitOnHitDecoratorTest {

    @Test
    void hitCausesForfeitAndPlayerStaysInPlace() {
        Board board = new Board(18, 3);
        Player p1 = new Player("P1", 1, "R");
        Player p2 = new Player("P2", 1, "B");

        p1.setProgress(1);
        p2.setProgress(3); // absolute pos = 4

        Rules rules = new ForfeitOnHitDecorator(new BasicRules());
        MoveResult mr = rules.apply(board, p1, 2, List.of(p1, p2));

        assertTrue(mr.hit(), "Should detect a HIT");
        assertEquals(p1.getProgress(), mr.fromProgress());
        assertEquals(mr.fromProgress(), mr.toProgress(), "Forfeit: stays in place");
        assertFalse(mr.overshoot());
        assertFalse(mr.won());
        assertEquals(1, p1.getProgress(), "Player must remain on the original square");
        assertEquals(0, p1.getTurnsTaken(), "Turn is forfeited, so turnsTaken is not incremented");
    }

    @Test
    void noHitDelegatesToInnerRules() {
        Board board = new Board(18, 3);
        Player p1 = new Player("P1", 1, "R");
        p1.setProgress(1);

        Rules rules = new ForfeitOnHitDecorator(new BasicRules());
        MoveResult mr = rules.apply(board, p1, 3, List.of(p1));

        assertFalse(mr.hit());
        assertEquals(1, mr.fromProgress());
        assertEquals(4, mr.toProgress());
        assertEquals(4, p1.getProgress());
        assertEquals(1, p1.getTurnsTaken());
    }
}
