package uk.ac.mmu.game.domain;

public interface GameStateObserver {
    void onStateChanged(Game game, String from, String to);
}
