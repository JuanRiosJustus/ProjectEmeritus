package main.game.components;

public class OverlayAnimation extends Component {
    private Animation animation = null;
    public void set(Animation anime) { animation = anime; }
    public boolean hasOverlay() { return animation != null; }
    public Animation getAnimation() { return animation; }
}
