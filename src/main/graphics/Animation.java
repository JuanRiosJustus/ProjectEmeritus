package main.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.engine.EngineController;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Animation {
    private final Image[] mFrames;
    private final Vector3f ephemeral = new Vector3f();
    private final float[] offset = new float[]{ 0, -1 }; // this is just an (x,y) vector
    private final float[] position = new float[]{ 0, 0 };
    private final static Random random = new Random();
    private int mCurrentFrame;
    private double mLoopDurationInSeconds = 1;
    private double mProgress = 0.0; // Value from 0.0 to 1.0 (one loop)

    public Animation(BufferedImage image) { this(new BufferedImage[]{ image }); }
    public Animation(BufferedImage[] images) {
        mFrames = new Image[images.length];
        for (int i = 0; i < images.length; i++) {
            BufferedImage image = images[i];
            Image newImage = SwingFXUtils.toFXImage(image, null);
            mFrames[i] = newImage;
        }

        // Initialize to a random starting frame
        mProgress = random.nextDouble();
        // Set loop duration to 1 seconds.
        mLoopDurationInSeconds = 1.0;
    }

    /**
     * Updates the animation based on elapsed time.
     * This method will make the animation complete one loop per 3 seconds.
     */
    public void update() {
        // Use the global deltaTime from EngineController (in seconds).
        double deltaTime = EngineController.getInstance().getDeltaTime();

        // Increment progress based on deltaTime.
        mProgress += deltaTime / mLoopDurationInSeconds;

        // Wrap around if progress exceeds 1.
        if (mProgress >= 1.0) {
            mProgress -= 1.0;
        }

        // Compute the frame index based on the current progress.
        int frameIndex = (int) (mProgress * mFrames.length);
        mCurrentFrame = Math.min(frameIndex, mFrames.length - 1);
    }

    public Image[] getFrames() { return mFrames; }
    public Image toImage() { return mFrames[mCurrentFrame]; }
    public Image getFrame(int index) { return mFrames[index]; }
    public int getNumberOfFrames() { return mFrames.length; }
    public void reset() { mCurrentFrame = 0; }
    public boolean hasCompletedLoop() { return mCurrentFrame >= mFrames.length - 1; }
    public int getCurrentFrame() { return mCurrentFrame; }

    public Vector3f getVector() { ephemeral.copy(position[0], position[1]); return ephemeral; }
}
