package main.game.components;

import main.constants.ColorPalette;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public class Tags extends Component {
    private static class TagData {
        public String name;
        public Object source;
        public int lifetimeTimeInTurns = 0;
        public TagData(String tagName, Object sourceRef) {
            name = tagName;
            source = sourceRef;
        }
    }

    public static final String SLEEP = "Sleep", YAWN = "Yawn";
    private final Map<String, TagData> tagMap = new HashMap<>();
    private static final Queue<String> toDeleteQueue = new LinkedList<>();
    private static final Queue<TagData> toAddQueue = new LinkedList<>();
    private final Map<String, Object> stage = new HashMap<>();
    private static final SplittableRandom random = new SplittableRandom();
    private boolean startOfTurnHandled = false;
    private boolean endOfTurnHandled = false;
    public void add(String tag, Object source) { tagMap.put(tag, new TagData(tag, source)); }
    public Object remove(String effect) { return tagMap.remove(effect); }
    public boolean contains(String effect) { return tagMap.containsKey(effect); }
    public void clear() { tagMap.clear(); }

    public Map<String, Object> getTagMap() {
        stage.clear();
        for (Map.Entry<String, TagData> entry : tagMap.entrySet()) {
            stage.put(entry.getKey(), entry.getValue().source);
        }
        return stage;
    }

    public void reset() {
        startOfTurnHandled = false;
        endOfTurnHandled = false;
    }

    public static void handleEndOfTurn(GameModel model, Entity unit) {
        Tags unitTags = unit.get(Tags.class);
        if (unitTags.endOfTurnHandled) { return; }
        toHandle(model, unit, false);
        unitTags.endOfTurnHandled = true;
    }

    public static void handleStartOfTurn(GameModel model, Entity unit) {
        Tags unitTags = unit.get(Tags.class);
        if (unitTags.startOfTurnHandled) { return; }
        toHandle(model, unit, true);
        unitTags.startOfTurnHandled = true;
    }

    private static void toHandle(GameModel model, Entity unit, boolean isStartOfTurn) {

        toAddQueue.clear();
        toDeleteQueue.clear();

        Vector unitPosition = unit.get(Animation.class).getVector();
        Tags unitTags = unit.get(Tags.class);

        for (Map.Entry<String, TagData> entry : unitTags.tagMap.entrySet()) {
            TagData data = entry.getValue();
            String key = entry.getKey();

            switch (key) {
                case YAWN -> {
                    if (isStartOfTurn) { continue; }
                    // The chance to remove the tag
                    if (random.nextBoolean()) { continue; }
                    toDeleteQueue.add(key);
                    // Chance to stop yawning and never go to sleep
                    if (random.nextBoolean()) { continue; }
                    toAddQueue.add(new TagData(SLEEP, YAWN));
                    model.system.floatingText.floater("Falls Asleep!", unitPosition, ColorPalette.WHITE);
                }
                case SLEEP -> {
                    if (isStartOfTurn) { continue; }
                    if (data.lifetimeTimeInTurns <= 1) { continue; }
                    // The chance to remove the tag
                    if (random.nextBoolean() && data.lifetimeTimeInTurns < 3) { continue; }
                    toDeleteQueue.add(key);
                    model.system.floatingText.floater("Awoke!", unitPosition, ColorPalette.WHITE);
                    model.logger.log(unit + " awakes from sleep");
                }
            }

            if (!isStartOfTurn) { data.lifetimeTimeInTurns += 1; }
        }

        // Remove a tag
        while (!toDeleteQueue.isEmpty()) {
            TagData toDelete = unitTags.tagMap.remove(toDeleteQueue.poll());
            model.system.floatingText.floater("-" + toDelete.name, unitPosition, ColorPalette.WHITE);
        }

        // Add the new tag
        while (!toAddQueue.isEmpty()) {
            TagData toAdd = toAddQueue.poll();
            unitTags.tagMap.put(toAdd.name, toAdd);
            model.system.floatingText.floater("+" + toAdd.name, unitPosition, ColorPalette.WHITE);
        }
    }
}
