package main.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.constants.Vector3f;

import java.awt.image.BufferedImage;
import java.util.Random;

public class AnimationV2 {
    private final Image[] content;
    private final Vector3f ephemeral = new Vector3f();
    private final float[] offset = new float[]{ 0, -1 }; // this is just an (x,y) vector
    private final float[] position = new float[]{ 0, 0 };
    private final static Random random = new Random();
    private int currentFrame;
    private int iterations;
    private int iterationSpeed;
    private double progress;
    public AnimationV2(BufferedImage image) { this(new BufferedImage[]{ image }); }
    public AnimationV2(BufferedImage[] images) {
        iterationSpeed = 3;//random.nextInt(2) + 2; // the higher, the faster the animation
        content = new Image[images.length];
        for (int i = 0; i < images.length; i++) {
            BufferedImage image = images[i];
            Image newImage = SwingFXUtils.toFXImage(image, null);
            content[i] = newImage;
        }
        currentFrame = random.nextInt(content.length);
        iterations = 0;
    }

    public void update() {
        if (iterationSpeed < 0) { return; }
        iterations++;
        if (content.length > 0 && iterations == iterationSpeed) {
            currentFrame++;
            iterations = 0;
        }
        if (currentFrame >= content.length - 1) {
            currentFrame = 0;
        }
    }

    public Image[] getContent() { return content; }
    public void setIterationSpeed(int speed) { iterationSpeed = speed; }
    public Image toImage() { return content[currentFrame]; }
    public Image getFrame(int index) { return content[index]; }
    public int getNumberOfFrames() { return content.length; }
    public void reset() { currentFrame = 0; }
    public boolean hasCompletedLoop() { return currentFrame >= content.length - 1; }
    public int getCurrentFrame() { return currentFrame; }
    public int getAnimatedOffsetX() { return (int) offset[0]; }
    public int getAnimatedOffsetY() { return (int) offset[1]; }

    public Vector3f getVector() { ephemeral.copy(position[0], position[1]); return ephemeral; }
}
