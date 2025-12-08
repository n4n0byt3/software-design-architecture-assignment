package uk.ac.mmu.game.domain;

/**
 * "InPlay" state in the Game state machine.
 * Responsible for executing a single logical turn:
 * - increment the current player's turn count
 * - roll the dice
 * - apply rules
 * - notify observers
 * - advance turn order or transition to GameOver
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

        // Count this as a turn attempt regardless of whether the move is forfeit
        current.incTurns();

        int roll = game.getDice().shake();
        MoveResult res = game.getRules().apply(
                game.getBoard(),
                current,
                roll,
                game.getPlayers()
        );
        game.record(res);

        // Notify observers that a move has been played
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
