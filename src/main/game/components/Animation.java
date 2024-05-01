package main.game.components;

import main.engine.Engine;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Animation extends Component {
    private final BufferedImage[] content;
    private final Vector3f ephemeral = new Vector3f();
    public final float[] offset = new float[]{ 0, -1 }; // this is just an (x,y) vector
    public final float[] position = new float[]{ 0, 0 };
    private final static Random random = new Random();
    private int currentFrame;
    private int iterations;
    private int iterationSpeed;
    private double progress;
    public Animation(BufferedImage image) { this(new BufferedImage[]{ image }); }
    public Animation(BufferedImage[] images) {
        iterationSpeed = 3;//random.nextInt(2) + 2; // the higher, the faster the animation
        content = new BufferedImage[images.length];
        System.arraycopy(images, 0, content, 0, images.length);
        currentFrame = random.nextInt(content.length);
        iterations = 0;
    }

    public void update() {
        if (iterationSpeed < 0) { return; }
        double framesUpdatedThisTick = Engine.getInstance().getDeltaTime() * 1.5;
        double framesToGoThrough = content.length;
        progress += framesUpdatedThisTick / framesToGoThrough;
//        int lerpVal = Vector.lerp()
        iterations++;
        if (iterations == iterationSpeed) {
            currentFrame++;
            iterations = 0;
            if (currentFrame != -1 && currentFrame < content.length / 2) {
                offset[1] -= 1;
            } else if (content.length / 2 < currentFrame) {
                offset[1] += 1;
            }
        }
        if (currentFrame == content.length) {
            currentFrame = 0;
            offset[0] = 0;
            offset[1] = -1;
        }
    }

    public BufferedImage[] getContent() { return content; }
    public void setIterationSpeed(int speed) { iterationSpeed = speed; }
    public BufferedImage toImage() { return content[currentFrame]; }
    public BufferedImage getFrame(int index) { return content[index]; }
    public int getNumberOfFrames() { return content.length; }
    public void reset() { currentFrame = 0; }
    public boolean hasCompletedLoop() { return currentFrame >= content.length - 1; }
    public int getCurrentFrame() { return currentFrame; }
    public int getAnimatedX() { return (int) (offset[0] + position[0]); }
    public int getAnimatedY() { return (int) (offset[1] + position[1]); }
    public int getAnimatedOffsetX() { return (int) offset[0]; }
    public int getAnimatedOffsetY() { return (int) offset[1]; }
    public void set(float x, float y) { position[0] = x; position[1] = y; }
    public Vector3f getVector() { ephemeral.copy(position[0], position[1]); return ephemeral; }
    public Animation copy() { return new Animation(content); }
}
