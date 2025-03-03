package main.game.main;

import main.logging.EmeritusLogger;

public class GameLoopManager {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(GameLoopManager.class);
    private long initialTime = System.nanoTime();
    private double deltaUpdate = 0.0D;
    private int frames = 0;
    private int lastFrameRate = 0;
    private long timer = System.currentTimeMillis();
    private long lastUpdateTime = System.nanoTime();
    private double deltaTime = 0;

    public void startLoop(long currentTime) {
        deltaUpdate += (double)(currentTime - initialTime) / 1.6666666E7D;
        initialTime = currentTime;
    }

    public boolean shouldUpdate() {
        return deltaUpdate >= 1.0D;
    }

    public void handleUpdate() {
        long currentTime = System.nanoTime();
        deltaTime = (double)(currentTime - lastUpdateTime) / 1.0E9D;
        deltaUpdate--;
        lastUpdateTime = currentTime;
    }

    public void endLoop() {
        if (System.currentTimeMillis() - timer > 1000L) {
            if (lastFrameRate != frames) {
//                logger.info(String.format("FPS: %d", frames));
                mLogger.info("FPS: {}", frames);
            }
//            System.out.println("Delta Time: " + deltaTime);
            lastFrameRate = frames;
            frames = 0;
            timer += 1000L;
        }
    }
}
