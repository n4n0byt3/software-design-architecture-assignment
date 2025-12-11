package uk.ac.mmu.game.domain;

public final class InPlayState implements GameState {

    @Override
    public String name() {
        return "InPlay";
    }

    @Override
    public MoveResult playTurn(Game game) {
        TurnOrder order = game.getTurnOrder();
        Player current = order.current();

        int roll = game.getDice().shake();
        MoveResult res = game.getRules().apply(
                game.getBoard(),
                current,
                roll,
                game.getPlayers()
        );
        game.record(res);

        // Lifecycle responsibility: count how many turns this player has taken.
        current.incTurns();

        // Notify observers that a move has been played (using the updated count).
        game.notifyTurnPlayed(current, res);

        if (res.won()) {
            game.switchTo(new GameOverState());
            game.notifyGameFinished(current);
        } else {
            order.next();
        }
        return res;
    }
}
