package uk.ac.mmu.game.domain;

public class Board {
    private final int mainSize;
    private final int tailSize;

    public Board(int mainSize, int tailSize) {
        this.mainSize = mainSize;
        this.tailSize = tailSize;
    }

    public int mainSize() { return mainSize; }
    public int tailSize() { return tailSize; }
    public int endProgress() { return mainSize + tailSize - 1; }

    public String labelFor(Player p, int progress) {
        if (progress == 0) return "Home (Position " + p.getHomeIndex() + ")";
        if (progress < mainSize) {
            int mainPos = ((p.getHomeIndex() - 1 + progress) % mainSize) + 1;
            return "Position " + mainPos;
        }
        int tailStep = progress - mainSize + 1;
        if (tailStep == tailSize) return "End (Position " + p.getColourLetter() + tailStep + ")";
        return "Tail Position " + p.getColourLetter() + tailStep;
    }

    public int mainRingPosFor(Player p, int progress) {
        return ((p.getHomeIndex() - 1 + progress) % mainSize) + 1;
    }
}
