package main.game.components;

import main.constants.MillisecondTimer;

import java.util.HashMap;
import java.util.Map;

public class TimerComponent extends Component {
    public static final String WAIT_BEFORE_USING_MOVE = "wait.before.using.move";
    public static final String WAIT_BEFORE_USING_ABILITY = "wait.before.using.ability";
    public static final String WAIT_BEFORE_ENDING_TURN = "wait.before.ending.turn";
    private static final String TURN_TIMER = "turn.timer";
    private final Map<String, MillisecondTimer> mTimers = new HashMap<>();

//    public void startTimer(String key) {
//        MillisecondTimer timer = mTimers.getOrDefault(key, new MillisecondTimer());
//        mTimers.put(key, timer);
//        timer.start();
//    }

    public void startTimer(String key) {
        MillisecondTimer timer = mTimers.getOrDefault(key, new MillisecondTimer());
        mTimers.put(key, timer);
        timer.start();
    }

    public void stopTimer(String key) {
        MillisecondTimer timer = mTimers.getOrDefault(key, new MillisecondTimer());
        mTimers.put(key, timer);
        timer.stop();
    }

    public void reset() {
        for (Map.Entry<String, MillisecondTimer> entry : mTimers.entrySet()) {
            entry.getValue().reset();
        }
    }

    public long getTimeElapsedMilliseconds(String key) {
        MillisecondTimer timer = mTimers.getOrDefault(key, new MillisecondTimer());
        mTimers.put(key, timer);
        return timer.getElapsedTime();
    }

    public boolean hasElapsedTime(String key, long milliseconds) {
        MillisecondTimer timer = mTimers.getOrDefault(key, new MillisecondTimer());
        mTimers.put(key, timer);
        return timer.getElapsedTime() >= milliseconds;
    }
}
