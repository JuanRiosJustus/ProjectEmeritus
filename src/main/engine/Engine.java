package main.engine;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.awt.*;

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
    // This is to get the default size of the title bar during load
    private final Insets mInsets = Toolkit.getDefaultToolkit().getScreenInsets(new Frame().getGraphicsConfiguration());
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
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        while (running) {
            startLoop();
            if (shouldUpdate()) {
                mController.input();
                mController.update();
                handleUpdate();
            }
            if (shouldRender()) {
//                Toolkit.getDefaultToolkit().sync();
                mController.render();
                handleRender();
            }
            getHeapData();
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

    private long mHeapSize = 0L;
    public static void getHeapData() {
        long currentHeapSize =  Runtime.getRuntime().totalMemory();
//        Engine.getInstance().logger.info("{} used", formatSize(currentHeapSize));
        if (currentHeapSize != mInstance.mHeapSize) {
            mInstance.mHeapSize = currentHeapSize;
            long heapMaxSize = Runtime.getRuntime().maxMemory();
            mInstance.logger.info("{} of {} used", formatSize(mInstance.mHeapSize), formatSize(heapMaxSize));
        }
//        long heapSize = Runtime.getRuntime().totalMemory();
//
//        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
//        long heapMaxSize = Runtime.getRuntime().maxMemory();
//
//        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
//        long heapFreeSize = Runtime.getRuntime().freeMemory();
//
//        System.out.println("heap size: " + formatSize(heapSize));
//        System.out.println("heap max size: " + formatSize(heapMaxSize));
//        System.out.println("heap free size: " + formatSize(heapFreeSize));
    }

    private static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
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

//    public int getHeaderSize() { return mInstance.getController().getView().getInsets().top; }
    public int getHeaderSize() { return mInsets.top; }

    public void stop(String message) {
        running = false;
        mController.mView.setVisible(false);
        logger.info(message);
        ELoggerFactory.getInstance().close();
        System.exit(0);
    }
}
