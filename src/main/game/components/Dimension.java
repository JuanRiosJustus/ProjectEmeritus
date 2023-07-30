package main.game.components;

public class Dimension extends Component {

    public static final Dimension temporary = new Dimension(0 , 0);

    public float width;
    public float height;

    public Dimension() {
        this(0, 0);
    }
    public Dimension(float startingWidth, float startingHeight) {
        width = startingWidth;
        height = startingHeight;
    }

    public void copy(float newWidth, float newHeight) {
        width = newWidth;
        height = newHeight;
    }

    public void copy(Dimension dimension) {
        copy(dimension.width, dimension.height);
    }
}
