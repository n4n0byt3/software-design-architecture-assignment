package uk.ac.mmu.game.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExactEndDecoratorTest {

    @Test
    void nonOvershootBehavesLikeInnerRules() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        red.setProgress(17);

        Rules rules = new ExactEndDecorator(new BasicRules());
        MoveResult mr = rules.apply(board, red, 1, List.of(red));

        assertEquals(17, mr.fromProgress());
        assertEquals(18, mr.toProgress());
        assertFalse(mr.overshoot());
        assertFalse(mr.won());
        assertEquals(18, red.getProgress());
        assertEquals(1, red.getTurnsTaken());
    }

    @Test
    void overshootIsForfeitAndPlayerStaysInPlace() {
        Board board = new Board(18, 3);
        Player red = new Player("Red", 1, "R");
        red.setProgress(19); // one before end

        Rules rules = new ExactEndDecorator(new BasicRules());
        MoveResult mr = rules.apply(board, red, 5, List.of(red));

        assertEquals(19, mr.fromProgress());
        assertEquals(19, mr.toProgress(), "Player should remain in place on overshoot");
        assertTrue(mr.overshoot());
        assertFalse(mr.won());
        assertEquals(19, red.getProgress(), "Progress must not change on overshoot-forfeit");
        assertEquals(0, red.getTurnsTaken(), "Turn is forfeited, so we do not increment turnsTaken here");
    }
}
