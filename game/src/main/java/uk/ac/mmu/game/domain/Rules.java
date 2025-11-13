package uk.ac.mmu.game.domain;

import java.util.List;

public interface Rules {
    MoveResult apply(Board board, Player current, int roll, List<Player> allPlayers);
}
