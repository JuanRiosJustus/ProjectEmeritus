package main.engine;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Engine {

    private boolean running;
    private long initialTime = System.nanoTime();
    private double deltaUpdate = 0.0D;
    private double deltaFrame = 0.0D;
    private int frames = 0;
    private int lastFrameRate = 0;
    private long timer = System.currentTimeMillis();
    private long lastUpdateTime = System.nanoTime();
    private double deltaTime;
    private long uptime = 0;
    private final EngineController mController = new EngineController();
    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
    private static Engine mInstance = null;
    private Engine() { }
    public static Engine getInstance() {
        if (mInstance == null) {
            mInstance = new Engine();
        }
        return mInstance;
    }

    public void run() {
        running = true;
        while (running) {
            startLoop();
            if (shouldUpdate()) {
                mController.input();
                mController.update();
                handleUpdate();
            }
            if (shouldRender()) {
                mController.render();
                handleRender();
            }
            endLoop();
        }
    }

    private boolean shouldUpdate() {
        return deltaUpdate >= 1.0D;
    }
    private void handleUpdate() {
        long currentTime = System.nanoTime();
        deltaTime = (double)(currentTime - lastUpdateTime) / 1.0E9D;
        deltaUpdate--;
        lastUpdateTime = currentTime;
    }
    private void startLoop() {
        long currentTime = System.nanoTime();
        deltaUpdate += (double)(currentTime - initialTime) / 1.6666666E7D;
        deltaFrame += (double)(currentTime - initialTime) / 1.6666666E7D;
        initialTime = currentTime;
    }
    private boolean shouldRender() {
        return deltaFrame >= 1.0D;
    }
    private void handleRender() {
        frames++;
        deltaFrame--;
    }

    private void endLoop() {
        if (System.currentTimeMillis() - timer > 1000L) {
            if (lastFrameRate != frames) {
                logger.info(String.format("FPS: %d", frames));
//                System.out.printf("FPS: %d%n", frames);
            }
            lastFrameRate = frames;
            frames = 0;
            timer += 1000L;
            uptime++;
        }
    }

    public int getViewWidth() { return mController.getView().getWidth(); }
    public int getViewHeight() { return mController.getView().getHeight(); }
    public EngineController getController() { return mController; }
    public double getFPS() { return lastFrameRate; }
    public double getDeltaTime() { return deltaTime; }
    public long getUptime() { return uptime; }
    public void stop() { stop("Exiting."); }

    public int getHeaderSize() { return mInstance.getController().getView().getInsets().top; }

    public void stop(String message) {
        running = false;
        mController.mView.setVisible(false);
        logger.info(message);
        ELoggerFactory.getInstance().close();
        System.exit(0);
    }
}
