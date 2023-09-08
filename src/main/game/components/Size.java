package main.game.components;

public class Size extends Component {

    public float width;
    public float height;

    public Size() {
        this(0, 0);
    }
    public Size(float startingWidth, float startingHeight) {
        width = startingWidth;
        height = startingHeight;
    }

    public void copy(float newWidth, float newHeight) {
        width = newWidth;
        height = newHeight;
    }

    public void copy(Size size) {
        copy(size.width, size.height);
    }
}
