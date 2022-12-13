package game.components;

import java.awt.image.BufferedImage;
import java.util.Random;

public class SpriteAnimation extends Component {

    private final BufferedImage[] content;
    private final int numberOfFrames;
    public final Vector offset = new Vector(0, -1);
    public final Vector position = new Vector();
    private final static Random randomizer = new Random();
    private final Dimension dimension;

    private int currentFrame;
    private int iterations;
    private int iterationSpeed;

    public SpriteAnimation(BufferedImage image) { this(new BufferedImage[]{ image }); }

    public SpriteAnimation(BufferedImage[] images) { this(images,randomizer.nextInt(7) + 3); }

    public SpriteAnimation(BufferedImage[] images, int timePerFrame) {
        iterationSpeed = timePerFrame; // the higher, the faster the animation
        content = new BufferedImage[images.length];
        System.arraycopy(images, 0, content, 0, images.length);
        currentFrame = 0;
        iterations = 0;
        numberOfFrames = content.length;
        BufferedImage current = content[currentFrame];
        dimension = new Dimension(current.getWidth(), current.getHeight());
    }


    public Vector update() {
        if (iterationSpeed < 0) { return offset; }
        iterations++;
        if (iterations == iterationSpeed) {
            currentFrame++;
            iterations = 0;
            if (currentFrame != -1 && currentFrame < content.length / 2) {
                offset.y -= 1;
            } else if (content.length / 2 < currentFrame) {
                offset.y += 1;
            }
        }
        if (currentFrame == numberOfFrames) {
            currentFrame = 0;
            offset.x= 0;
            offset.y = -1;
        }
        dimension.width = content[currentFrame].getWidth();
        dimension.height = content[currentFrame].getHeight();
        return offset;
    }

    public void setIterationSpeed(int speed) { iterationSpeed = speed; }
    public BufferedImage toImage() { return content[currentFrame]; }
    public BufferedImage getFrame(int index) { return content[index]; }
    public Dimension size() { return dimension; }
    public int getNumberOfFrames() { return numberOfFrames; }
    public void reset() { currentFrame = 0; }
    public int getCurrentFrame() { return currentFrame; }
    public int animatedX() { return (int) (offset.x + position.x); }
    public int animatedY() { return (int) (offset.y + position.y); }
    public SpriteAnimation copy() {
        return new SpriteAnimation(content.clone(), iterationSpeed);
    }

}
