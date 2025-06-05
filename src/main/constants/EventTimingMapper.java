package main.constants;

public class EventTimingMapper {
    private final LRUCache<String, UtilityTimer> mEventTimers = new LRUCache<>();

    public boolean startEvent(String eventName) {
        // Check if the event is present. If not, create a new event to start tracking duration
        UtilityTimer utilityTimer = mEventTimers.get(eventName);
        if (utilityTimer != null) {
            // the event is already running
            if (utilityTimer.isRunning()) { return false; }
            utilityTimer.reset();
        } else {
            utilityTimer = new UtilityTimer();
        }

        utilityTimer.start();
        mEventTimers.put(eventName, utilityTimer);
        return true;
    }

    public double getEventDurationInSeconds(String eventName) {
        UtilityTimer utilityTimer = mEventTimers.get(eventName);
        if (utilityTimer == null) { return -1; }
        return utilityTimer.getElapsedSeconds();
    }

    public long getEventDurationInMilliSeconds(String eventName) {
        UtilityTimer utilityTimer = mEventTimers.get(eventName);
        if (utilityTimer == null) { return -1; }
        return utilityTimer.getElapsedMilliSeconds();
    }

    public void endEvent(String eventName) {
        // Check if the event is present. If it is, check that an a
        UtilityTimer utilityTimer = mEventTimers.get(eventName);
        if (utilityTimer == null) { return; }
        utilityTimer.stop();
    }

    public static void main(String[] args) throws InterruptedException {
        EventTimingMapper eventTimingMapper = new EventTimingMapper();

        // Test 1: Start an event
        System.out.println("Test 1: Start an event");
        boolean started = eventTimingMapper.startEvent("TestEvent");
        System.out.println("Event started: " + started); // Expected: true

        // Test 2: Get event duration immediately after starting
        System.out.println("\nTest 2: Get event duration immediately after starting");
        double durationSeconds = eventTimingMapper.getEventDurationInSeconds("TestEvent");
        System.out.println("Duration in seconds: " + durationSeconds); // Expected: Very close to 0

        // Test 3: Wait and get event duration
        System.out.println("\nTest 3: Wait and get event duration");
        Thread.sleep(1500); // Wait for 1.5 seconds
        durationSeconds = eventTimingMapper.getEventDurationInSeconds("TestEvent");
        System.out.println("Duration in seconds after 1.5 seconds: " + durationSeconds); // Expected: Close to 1.5

        long durationMilliseconds = eventTimingMapper.getEventDurationInMilliSeconds("TestEvent");
        System.out.println("Duration in milliseconds: " + durationMilliseconds); // Expected: Close to 1500

        // Test 4: End an event
        System.out.println("\nTest 4: End an event");
        eventTimingMapper.endEvent("TestEvent");
        durationSeconds = eventTimingMapper.getEventDurationInSeconds("TestEvent");
        System.out.println("Duration after stopping the event: " + durationSeconds); // Expected: Same as previous duration

        // Test 5: Restart an event
        System.out.println("\nTest 5: Restart an event");
        started = eventTimingMapper.startEvent("TestEvent");
        System.out.println("Event restarted: " + started); // Expected: true
        Thread.sleep(1000); // Wait for 1 second
        durationSeconds = eventTimingMapper.getEventDurationInSeconds("TestEvent");
        System.out.println("Duration after restarting and 1 second: " + durationSeconds); // Expected: Close to 1

        // Test 6: Handle non-existent event
        System.out.println("\nTest 6: Handle non-existent event");
        durationSeconds = eventTimingMapper.getEventDurationInSeconds("NonExistentEvent");
        System.out.println("Duration of non-existent event: " + durationSeconds); // Expected: -1
    }
}
