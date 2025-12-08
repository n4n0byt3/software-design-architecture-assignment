package uk.ac.mmu.game.domain;

public interface PlayerTurnObserver {
    void onTurnPlayed(Game game, MoveResult result, Player currentPlayer);
}
