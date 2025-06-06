package main.constants;

import main.game.components.Component;

public class SecondTimer extends Component {

    private long start = System.nanoTime();

    public double elapsed() { return (System.nanoTime() - start) / 1_000_000_000F; }

    public void reset() { start = System.nanoTime(); }
}
