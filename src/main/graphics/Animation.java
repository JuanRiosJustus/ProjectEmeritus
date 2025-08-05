package main.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.constants.Vector3f;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Animation {
    private static class Metadata {
        private String mSheet;
        private int mWidth;
        private int mHeight;
        private String mEffect;
        public Metadata(String sheet, int width, int height, String effect) {
            mSheet = sheet;
            mWidth = width;
            mHeight = height;
            mEffect = effect;
        }
    }
    private static final double FRAMES_PER_SECOND = 60;
    private final Image[] mFrames;
    private final Vector3f ephemeral = new Vector3f();
    private final float[] offset = new float[]{ 0, -1 }; // this is just an (x,y) vector
    private final float[] position = new float[]{ 0, 0 };
    private final static Random random = new Random();
    private int mCurrentFrame;
    private double mLoopDurationInSeconds = 1;
    private double mProgress = 0.0; // Value from 0.0 to 1.0 (one loop)

    private static final double DEFAULT_ITERATION_SPEED_MULTIPLIER = 1.0f;
    private double mIterationSpeedMultiplier = 1.0f; // Default is normal speed

    private Metadata mMetadata;
    private double mFrameOpacity;
    public Animation(BufferedImage[] images) {
        mFrames = new Image[images.length];
        for (int i = 0; i < images.length; i++) {
            BufferedImage image = images[i];
            Image newImage = SwingFXUtils.toFXImage(image, null);
            mFrames[i] = newImage;
        }

        // Initialize to a random starting frame
        mProgress = random.nextDouble();

        // 60 FPS
        mLoopDurationInSeconds = mFrames.length / FRAMES_PER_SECOND;

        // How fast frames are changes, essentially
        mIterationSpeedMultiplier = DEFAULT_ITERATION_SPEED_MULTIPLIER;
    }

    public void setMetadata(String sheet, int width, int height, String effect) {
        mMetadata = new Metadata(sheet, width, height, effect);
    }

    public void setOpacity(float opacity) {
        if (opacity > 1 || opacity < 0 || opacity == mFrameOpacity) { return; }
        mFrameOpacity = opacity;

        for (int i = 0; i < mFrames.length; i++) {
            Image frame = mFrames[i];
            BufferedImage convertedFrame = SwingFXUtils.fromFXImage(frame, null);
            BufferedImage newFrame = ImageUtils.getResizedImage(
                    convertedFrame,
                    convertedFrame.getWidth(),
                    convertedFrame.getHeight(),
                    opacity
            );
            mFrames[i] = SwingFXUtils.toFXImage(newFrame, null);
        }
    }
    public void resetOpacityToDefault() { setSpeed(1); }


    public void setSpeed(float speed) {
        if (mIterationSpeedMultiplier != 1.0f) { return; } // If the multiplier was set, cannot set again atm
        mIterationSpeedMultiplier = speed;
    }
    public void resetSpeedToDefault() { mIterationSpeedMultiplier = DEFAULT_ITERATION_SPEED_MULTIPLIER; }

    /**
     * Updates the animation based on elapsed time.
     * This method will make the animation complete one loop per 3 seconds.
     */
    public void update(double deltaTime) {
        // Use the global deltaTime from EngineController (in seconds)

        // Increment progress based on deltaTime.
        mProgress += (deltaTime * mIterationSpeedMultiplier) / mLoopDurationInSeconds;

        // Wrap around if progress exceeds 1.
        if (mProgress >= 1.0) {
            mProgress = 0;
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
    public double getProgress() { return mProgress; }

    public Vector3f getVector() { ephemeral.copy(position[0], position[1]); return ephemeral; }
}
