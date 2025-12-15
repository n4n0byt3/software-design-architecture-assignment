package uk.ac.mmu.game.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple cyclic turn order for N players.
 */
public class TurnOrder {

    private final List<Player> players;
    private int index = 0;

    public TurnOrder(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("players are required");
        }
        // Defensive copy to avoid external mutation.
        this.players = new ArrayList<>(players);
    }

    public Player current() {
        return players.get(index);
    }

    public Player next() {
        index = (index + 1) % players.size();
        return current();
    }

    public List<Player> all() {
        return Collections.unmodifiableList(players);
    }
}
