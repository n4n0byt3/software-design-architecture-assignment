package uk.ac.mmu.game.domain;

import java.util.List;

/**
 * Simple cyclic turn order for N players.
 */
public class TurnOrder {

    private final List<Player> players;
    private int idx = 0;

    public TurnOrder(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("players are required");
        }
        this.players = players;
    }

    public Player current() {
        return players.get(idx);
    }

    public Player next() {
        idx = (idx + 1) % players.size();
        return current();
    }

    public List<Player> all() {
        return players;
    }
}
