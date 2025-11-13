package uk.ac.mmu.game.domain;

import java.util.List;

public class BasicRules implements Rules {
    @Override
    public MoveResult apply(Board board, Player p, int roll, List<Player> all) {
        int end = board.endProgress();
        int from = p.getProgress();
        int to = from + roll;
        boolean overshoot = to > end;
        if (overshoot) to = end;

        boolean hit = false;
        String victimName = null;
        Integer victimPosAbs = null;

        if (from < board.mainSize() && to < board.mainSize()) {
            int targetPos = board.mainRingPosFor(p, to);
            for (Player other : all) {
                if (other == p) continue;
                if (other.getProgress() < board.mainSize()) {
                    int otherPos = board.mainRingPosFor(other, other.getProgress());
                    if (otherPos == targetPos) {
                        hit = true;
                        victimName = other.getName();
                        victimPosAbs = otherPos;
                        break;
                    }
                }
            }
        }

        p.setProgress(to);
        p.incTurns();
        boolean won = (to == end);

        return new MoveResult(
                p.getName(), roll, from, to,
                hit, overshoot, won, "",
                victimName, victimPosAbs
        );
    }
}
