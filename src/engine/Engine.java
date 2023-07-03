package engine;

import logging.Logger;
import logging.LoggerFactory;

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
    public final EngineController controller = new EngineController();
    private final Logger logger = LoggerFactory.instance().logger(getClass());

    private static Engine instance = null;
    public static Engine instance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    public void run() {
        running = true;
        while (running) {
            startLoop();
            if (shouldUpdate()) {
                controller.input();
                controller.update();
                handleUpdate();
            }
            if (shouldRender()) {
                controller.render();
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
            }
            lastFrameRate = frames;
            frames = 0;
            timer += 1000L;
            uptime++;
        }
    }

    public double getFPS() { return lastFrameRate; }
    public double getDeltaTime() { return deltaTime; }
    public long getUptime() { return uptime; }

    public void stop() { stop("Exiting."); }

    public void stop(String message) {
        running = false;
        controller.view.setVisible(false);
        logger.info(message);
        LoggerFactory.instance().close();
        System.exit(0);
    }
}
