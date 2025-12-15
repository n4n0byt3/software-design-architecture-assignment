package uk.ac.mmu.game.domain;

/**
 * Represents a player and their state.
 *
 * <p>Fields:
 * <ul>
 *   <li>name - e.g. "Red"</li>
 *   <li>homeIndex - absolute starting square on the ring (e.g. 1, 10, 19, 28)</li>
 *   <li>colourLetter - used for tail labels (e.g. "R")</li>
 *   <li>progress - abstract progress around board (0..endProgress)</li>
 *   <li>turnsTaken - number of non-forfeited turns taken (counted by InPlayState)</li>
 * </ul>
 */
public class Player {

    private final String name;
    private final int homeIndex;
    private final String colourLetter;

    // 0..board.endProgress()
    private int progress = 0;

    // Turn counting is intentionally done at Game/InPlayState, not in rules.
    private int turnsTaken = 0;

    public Player(String name, int homeIndex, String colourLetter) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (homeIndex <= 0) {
            throw new IllegalArgumentException("homeIndex must be positive");
        }
        if (colourLetter == null || colourLetter.isBlank()) {
            throw new IllegalArgumentException("colourLetter is required");
        }

        this.name = name;
        this.homeIndex = homeIndex;
        this.colourLetter = colourLetter;
    }

    public String getName() {
        return name;
    }

    public int getHomeIndex() {
        return homeIndex;
    }

    public String getColourLetter() {
        return colourLetter;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTurnsTaken() {
        return turnsTaken;
    }

    public void incTurns() {
        turnsTaken++;
    }

    public boolean isAtEnd(Board board) {
        return progress == board.endProgress();
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", homeIndex=" + homeIndex +
                ", colourLetter='" + colourLetter + '\'' +
                ", progress=" + progress +
                ", turnsTaken=" + turnsTaken +
                '}';
    }
}
