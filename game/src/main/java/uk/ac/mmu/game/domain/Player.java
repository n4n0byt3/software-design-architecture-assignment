package uk.ac.mmu.game.domain;

public class Player {
    private final String name;
    private final int homeIndex;
    private final String colourLetter;
    private int progress = 0;
    private int turnsTaken = 0;

    public Player(String name, int homeIndex, String colourLetter) {
        this.name = name;
        this.homeIndex = homeIndex;
        this.colourLetter = colourLetter;
    }

    public String getName() { return name; }
    public int getHomeIndex() { return homeIndex; }
    public String getColourLetter() { return colourLetter; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public int getTurnsTaken() { return turnsTaken; }
    public void incTurns() { this.turnsTaken++; }
    public boolean isAtEnd(Board board) { return progress == board.endProgress(); }
}
