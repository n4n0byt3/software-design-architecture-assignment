package uk.ac.mmu.game.domain;

import java.util.List;

public class ExactEndDecorator implements Rules {
    private final Rules inner;
    public ExactEndDecorator(Rules inner) { this.inner = inner; }

    @Override
    public MoveResult apply(Board board, Player p, int roll, List<Player> all) {
        int end = board.endProgress();
        int from = p.getProgress();
        int to = from + roll;
        if (to > end) {
            // forfeit: remain; mark overshoot, no victim
            return new MoveResult(p.getName(), roll, from, from, false, true, false, "", null, null);
        }
        return inner.apply(board, p, roll, all);
    }
}
