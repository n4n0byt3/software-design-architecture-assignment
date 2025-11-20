package uk.ac.mmu.game.domain;

import java.util.List;

public class BasicRules implements Rules {

    @Override
    public MoveResult apply(Board board, Player p, int roll, List<Player> all) {
        int end = board.endProgress();
        int from = p.getProgress();
        int to = from + roll;
        boolean overshoot = to > end;

        if (overshoot) {
            to = end;
        }

        // Use Value Object to detect hit in a single, centralised place
        HitInfo hitInfo = HitInfo.detect(board, p, to, all);

        p.setProgress(to);
        p.incTurns();
        boolean won = (to == end);

        return new MoveResult(
                p.getName(),
                roll,
                from,
                to,
                hitInfo.hit(),
                overshoot,
                won,
                "",
                hitInfo.victimName(),
                hitInfo.victimPosAbs()
        );
    }
}
