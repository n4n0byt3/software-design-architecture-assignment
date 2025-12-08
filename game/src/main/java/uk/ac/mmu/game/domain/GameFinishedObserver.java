package uk.ac.mmu.game.domain;

public interface GameFinishedObserver {
    void onGameFinished(Game game, Player winner, int totalTurns, int winnerTurns);
}
