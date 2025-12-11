package uk.ac.mmu.game.domain;

/**
 * Represents a single player in the game.
 * Each player has:
 * - a name (e.g. "Red")
 * - a home index on the main ring (e.g. 1 or 10)
 * - a colour letter used in tail labels (e.g. "R", "B").
 *
 * Contract:
 * - name and colourLetter must be non-blank.
 * - homeIndex must be positive.
 * - progress is managed via the Game/Rules layers and must always be
 *   between 0 and board.endProgress() for any valid board.
 */
public final class Player {

    private final String name;
    private final int homeIndex;
    private final String colourLetter;

    // 0..board.endProgress()
    private int progress = 0;
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
        this.turnsTaken++;
    }

    public boolean isAtEnd(Board board) {
        return progress == board.endProgress();
    }

    @Override
    public String toString() {
        return String.format(
                "Player{name='%s', homeIndex=%d, colourLetter='%s', progress=%d, turnsTaken=%d}",
                name, homeIndex, colourLetter, progress, turnsTaken
        );
    }
}
