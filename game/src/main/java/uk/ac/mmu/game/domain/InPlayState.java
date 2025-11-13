package uk.ac.mmu.game.domain;

import java.util.List;

public class InPlayState implements GameState {

    @Override
    public String name() { return "InPlay"; }

    @Override
    public MoveResult playTurn(Game game) {
        TurnOrder order = game.getTurnOrder();
        Player current = order.current();

        int roll = game.getDice().shake();
        MoveResult res = game.getRules().apply(game.getBoard(), current, roll, game.getPlayers());
        game.record(res);

        if (res.won()) {
            game.switchTo(new GameOverState());
        } else {
            order.next();
        }
        return res;
    }
}
