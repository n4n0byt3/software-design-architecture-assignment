package uk.ac.mmu.game.domain;

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

        // Notify observers that a move has been played
        game.notifyTurnPlayed(current, res);

        if (res.won()) {
            // Transition to GameOver and notify observers
            game.switchTo(new GameOverState());
            game.notifyGameFinished(current);
        } else {
            order.next();
        }
        return res;
    }
}
