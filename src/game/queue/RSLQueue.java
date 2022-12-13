package game.queue;


import game.components.statistics.Statistics;
import game.entity.Entity;
import game.stats.node.ScalarNode;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class RSLQueue {

    private static class SpeedMeter {
        Entity unit;
        double amountTraveled;
    }

    public List<Entity> listOfUnits = new ArrayList<>();
    private final List<SpeedMeter> listOfSpeedMeters = new ArrayList<>();
    private final PriorityQueue<SpeedMeter> queueOfSpeedMeters = new PriorityQueue<>(10, (o1, o2) -> {
        double traveled1 = o1.amountTraveled;
        double traveled2 = o2.amountTraveled;
        return (int) (traveled2 - traveled1);
    });

    private double amountToTravel = 0;

    public void add(Entity unit) {
        SpeedMeter meter = new SpeedMeter();
        meter.unit = unit;
        meter.amountTraveled = 0;
        listOfSpeedMeters.add(meter);
        queueOfSpeedMeters.add(meter);
        listOfUnits.add(unit);
        int fastest = getFastest(listOfSpeedMeters);
        amountToTravel = listOfSpeedMeters.size() * fastest;
    }

    public void addAll(Entity[] units) {
        for (Entity c : units) {
            add(c);
        }
    }

    private int getFastest(List<SpeedMeter> meters) {
        int fastest = Integer.MIN_VALUE;
        for (SpeedMeter meter : meters) {
            Statistics stats = meter.unit.get(Statistics.class);
            ScalarNode node = stats.getScalarNode("speed");
            int speed = node.getTotal();
            if (speed > fastest) { fastest = speed; }
        }
        return fastest;
    }

    private void incrementSpeedMeters() {
        // while there are no champions with the amount needed to fill,
        // increment each by there speed stat once
        queueOfSpeedMeters.clear();
        for (SpeedMeter meter : listOfSpeedMeters) {
            Statistics stats = meter.unit.get(Statistics.class);
            ScalarNode node = stats.getScalarNode("speed");
            int speed = node.getTotal();
            meter.amountTraveled += speed;
            queueOfSpeedMeters.add(meter);
        }
    }

    public void requeue() {
        SpeedMeter meter = queueOfSpeedMeters.peek();
        if (meter == null) { return; }
        meter = queueOfSpeedMeters.poll();
        meter.amountTraveled = 0;
        queueOfSpeedMeters.add(meter);
    }

    public Entity peek() {
        SpeedMeter meter = queueOfSpeedMeters.peek();
        if (meter == null || meter.unit == null) { return null; }
        return meter.unit;
    }
    
    public Entity update() {
        SpeedMeter meter = queueOfSpeedMeters.peek();
        if (meter == null || meter.unit == null) { return null; }
        while(meter.amountTraveled < amountToTravel) {
            incrementSpeedMeters();
            meter = queueOfSpeedMeters.peek();
        }
        return meter.unit;
    }

    public double getPercentToFull(Entity c) {
        SpeedMeter meter = listOfSpeedMeters
                .stream()
                .filter(s -> s.unit == c)
                .findFirst().orElse(null);
        if (meter == null) { return -1; }
        double value = meter.amountTraveled / amountToTravel;
        return value > 1 ? 1 : value;
    }

    public List<Entity> getListOfCreatures() { return listOfUnits; }
}