package uk.ac.mmu.game.domain;

import java.util.List;

public class ForfeitOnHitDecorator implements Rules {
    private final Rules inner;
    public ForfeitOnHitDecorator(Rules inner) { this.inner = inner; }

    @Override
    public MoveResult apply(Board board, Player p, int roll, List<Player> all) {
        int from = p.getProgress();
        int end = board.endProgress();
        int candidateTo = Math.min(from + roll, end);

        boolean wouldHit = false;
        String victimName = null;
        Integer victimPosAbs = null;

        if (candidateTo < board.mainSize()) {
            int targetPos = board.mainRingPosFor(p, candidateTo);
            for (Player o : all) {
                if (o == p) continue;
                if (o.getProgress() < board.mainSize()) {
                    int otherPos = board.mainRingPosFor(o, o.getProgress());
                    if (otherPos == targetPos) {
                        wouldHit = true;
                        victimName = o.getName();
                        victimPosAbs = otherPos;
                        break;
                    }
                }
            }
        }

        if (wouldHit) {
            // forfeit on hit: remain at 'from'
            return new MoveResult(p.getName(), roll, from, from, true, false, false, "", victimName, victimPosAbs);
        }
        return inner.apply(board, p, roll, all);
    }
}
