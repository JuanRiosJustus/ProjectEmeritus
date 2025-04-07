package main.constants;

public class MillisecondTimer {
    private long startTime;
    private long elapsedTime;
    private boolean running;

    public MillisecondTimer() {
        this.elapsedTime = 0;
        this.running = false;
    }

    /**
     * Starts the timer. If already running, this call has no effect.
     */
    public void start() {
        if (running) { return; }
        startTime = System.currentTimeMillis();
        running = true;
    }

    /**
     * Stops the timer. If the timer is not running, this call has no effect.
     */
    public void stop() {
        if (!running) { return; }
        elapsedTime += System.currentTimeMillis() - startTime;
        running = false;
    }

    /**
     * Returns the total elapsed time in milliseconds.
     * If the timer is running, it includes the time since start was last called.
     */
    public long getElapsedTime() {
        if (running) {
            return elapsedTime + (System.currentTimeMillis() - startTime);
        } else {
            return elapsedTime;
        }
    }

    /**
     * Returns the total elapsed time in seconds.
     */
    public double getElapsedSeconds() {
        return getElapsedTime() / 1000.0;
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        elapsedTime = 0;
        running = false;
    }

    public static void main(String[] args) throws InterruptedException {
        MillisecondTimer millisecondTimer = new MillisecondTimer();
        millisecondTimer.start();
        // Simulate some work:
        System.out.println("Elapsed Time: " + millisecondTimer.getElapsedTime() + " milliseconds");
        Thread.sleep(1500);
        millisecondTimer.stop();
        System.out.println("Elapsed Time: " + millisecondTimer.getElapsedTime() + " milliseconds");
        System.out.println("Elapsed Time: " + millisecondTimer.getElapsedSeconds() + " seconds");
    }
}