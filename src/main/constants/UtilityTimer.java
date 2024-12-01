package main.constants;

import main.game.components.Component;

public class UtilityTimer extends Component {

    private long mStartTime; // The start time in milliseconds
    private long mEndTime;   // The end time in milliseconds
    private boolean mIsRunning; // Flag to check if the timer is running

    // Constructor
    public UtilityTimer() { reset(); }

    // Starts the timer
    public void start() {
        mStartTime = System.currentTimeMillis();
        mIsRunning = true;
        mEndTime = 0; // Reset the end time
    }

    // Stops the timer
    public void stop() {
        if (!mIsRunning) { return; }
        mEndTime = System.currentTimeMillis();
        mIsRunning = false;
    }

    // Resets the timer
    public void reset() {
        mStartTime = 0;
        mEndTime = 0;
        mIsRunning = false;
    }

    // Checks if the timer is running
    public boolean isRunning() { return mIsRunning; }

    // Gets the elapsed time in milliseconds
    public long getElapsedMilliSeconds() {
        if (mIsRunning) {
            return System.currentTimeMillis() - mStartTime;
        } else {
            return mEndTime - mStartTime;
        }
    }

    // Gets the elapsed time in seconds
    public double getElapsedSeconds() { return getElapsedMilliSeconds() / 1000.0; }

    // Gets the elapsed time in a formatted string (e.g., HH:MM:SS)
    public String getFormattedTime() {
        long elapsed = getElapsedMilliSeconds();
        long seconds = elapsed / 1000 % 60;
        long minutes = elapsed / (1000 * 60) % 60;
        long hours = elapsed / (1000 * 60 * 60);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Static helper: Delays for the specified number of milliseconds
    public static void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Main method to test the Timer class
    public static void main(String[] args) {
        UtilityTimer utilityTimer = new UtilityTimer();
        System.out.println("Starting timer...");
        utilityTimer.start();

        // Simulate some work
        UtilityTimer.delay(2500); // Delay for 2.5 seconds

        utilityTimer.stop();
        System.out.println("Timer stopped.");
        System.out.println("Elapsed time (ms): " + utilityTimer.getElapsedMilliSeconds());
        System.out.println("Elapsed time (s): " + utilityTimer.getElapsedSeconds());
        System.out.println("Formatted time: " + utilityTimer.getFormattedTime());
    }
}