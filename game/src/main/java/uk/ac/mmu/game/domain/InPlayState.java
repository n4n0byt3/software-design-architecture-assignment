package uk.ac.mmu.game.domain;

/**
 * In-play state: performs normal turn progression until a win condition is met.
 */
public class InPlayState implements GameState {

    @Override
    public String name() {
        return "InPlay";
    }

    @Override
    public MoveResult playTurn(Game game) {
        TurnOrder order = game.getTurnOrder();
        Player current = order.current();

        int roll = game.getDice().shake();
        MoveResult result = game.getRules().apply(game.getBoard(), current, roll, game.getPlayers());

        // Count turns here so forfeits can skip counting.
        if (!result.forfeited()) {
            current.incTurns();
        }

        game.record(result);
        game.notifyTurnPlayed(current, result);

        if (result.won()) {
            game.switchTo(new GameOverState());
            game.notifyGameFinished(current);
            return result;
        }

        order.next();
        return result;
    }
}
