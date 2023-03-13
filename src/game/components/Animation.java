package game.components;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Animation extends Component {

    private final BufferedImage[] content;
    public final Vector offset = new Vector(0, -1);
    public final Vector position = new Vector();
    private final static Random random = new Random();
    private int currentFrame;
    private int iterations;
    private int iterationSpeed;
    public Animation(BufferedImage image) { this(new BufferedImage[]{ image }); }
    public Animation(BufferedImage[] images) {
        iterationSpeed = 3;//random.nextInt(2) + 2; // the higher, the faster the animation
        content = new BufferedImage[images.length];
        System.arraycopy(images, 0, content, 0, images.length);
        currentFrame = random.nextInt(content.length);
        iterations = 0;
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
        if (currentFrame == content.length) {
            currentFrame = 0;
            offset.x= 0;
            offset.y = -1;
        }
        return offset;
    }

    public BufferedImage[] getContent() { return content; }
    public void setIterationSpeed(int speed) { iterationSpeed = speed; }
    public BufferedImage toImage() { return content[currentFrame]; }
    public BufferedImage getFrame(int index) { return content[index]; }
    public int getNumberOfFrames() { return content.length; }
    public void reset() { currentFrame = 0; }
    public boolean hasCompletedLoop() { return currentFrame >= content.length - 1; }
    public int getCurrentFrame() { return currentFrame; }
    public int animatedX() { return (int) (offset.x + position.x); }
    public int animatedY() { return (int) (offset.y + position.y); }
    public Animation copy() { return new Animation(content.clone()); }

}
