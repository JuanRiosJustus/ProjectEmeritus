package main.game.pathfinding.shadowcasting;

public class Shadow {
    int start;
    int end;
    public Shadow(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(Shadow other) {
        return start <= other.start && end >= other.end;
    }
}