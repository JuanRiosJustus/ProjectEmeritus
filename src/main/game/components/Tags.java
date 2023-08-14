package main.game.components;

import main.constants.ColorPalette;
import main.game.entity.Entity;
import main.game.main.GameModel;

import java.util.*;

public class Tags extends Component {
    private final Map<String, Object> tagToSource = new HashMap<>();
    private final Map<String, Integer> tagToTurn = new HashMap<>();
    private final Map<String, Object> stage = new HashMap<>();
    private final SplittableRandom random = new SplittableRandom();
    private boolean startOfTurnHandled = false;
    private boolean endOfTurnHandled = false;

    public void add(String tag, Object source) {
//        // Only can be averse by 1 type
//        if (tag.contains("Averse")) {
//            Queue<String> garbage = new LinkedList<>();
//            for (Map.Entry<String, Object> effect2 : tagToSource.entrySet()) {
//                if (!effect2.getKey().contains("Averse")) { continue; }
//                garbage.add(effect2.getKey());
//            }
//            while (!garbage.isEmpty()) { tagToSource.remove(garbage.poll()); }
//        }
        tagToSource.put(tag, source);
    }


    public Object remove(String effect) { return tagToSource.remove(effect); }
    public boolean contains(String effect) { return tagToSource.containsKey(effect); }
    public void clear() { tagToSource.clear(); }

    public Map<String, Object> getTags() {
        stage.clear();
        stage.putAll(tagToSource);
        return stage;
    }

    public void reset() {
        startOfTurnHandled = false;
        endOfTurnHandled = false;
    }
    public boolean shouldHandleStartOfTurn() { return !startOfTurnHandled; }
    public boolean shouldHandleEndOfTurn() { return !endOfTurnHandled; }

    public void handleEndOfTurn(GameModel model, Entity unit) {
        if (endOfTurnHandled) { return; }
        toHandle(model, unit, new String[]{"Yawn"});
        endOfTurnHandled = true;
    }

    public void handleStartOfTurn(GameModel model, Entity unit) {
        if (startOfTurnHandled) { return; }
        toHandle(model, unit, new String[]{"Sleep"});
        startOfTurnHandled = true;
    }

    private void toHandle(GameModel model, Entity unit, String[] tags) {

        Queue<String> toDelete = new LinkedList<>();
        Queue<String[]> toAdd = new LinkedList<>();
        Vector position = unit.get(Animation.class).position;

        for (Map.Entry<String, Object> entry : tagToSource.entrySet()) {
            tagToTurn.put(entry.getKey(), tagToTurn.getOrDefault(entry.getKey(), 0));

            switch (entry.getKey()) {
                case "Yawn" -> {
                    toDelete.add(entry.getKey());
                    if (random.nextBoolean()) {
                        model.system.floatingText.floater("zZzZ", position, ColorPalette.WHITE);
                        model.logger.log(unit + " is falling asleep");
                        toAdd.add(new String[]{"Sleep", "Yawn"});
                    }
                }
                case "Sleep" -> {
                    toDelete.add("Yawn");
                    if ((random.nextBoolean() && tagToTurn.get("Sleep") > 0) || tagToTurn.get("Sleep") >= 2) {
                        toDelete.add("Sleep");
                        model.system.floatingText.floater("Awoke!", position, ColorPalette.WHITE);
                        model.logger.log(unit + " awakes from sleep");
                    }
                }
            }

            tagToTurn.put(entry.getKey(), tagToTurn.getOrDefault(entry.getKey(), 0) + 1);
        }

        while (!toDelete.isEmpty()) {
            String deleting = toDelete.poll();
            tagToSource.remove(deleting);
            tagToTurn.remove(deleting);
        }

        while (!toAdd.isEmpty()) {
            String[] adding = toAdd.poll();
            tagToSource.put(adding[0], adding[1]);
        }
    }
}
