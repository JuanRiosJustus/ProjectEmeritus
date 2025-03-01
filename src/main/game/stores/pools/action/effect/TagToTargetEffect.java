package main.game.stores.pools.action.effect;

import main.constants.Quadruple;
import main.constants.Tuple;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.ColorPalette;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TagToTargetEffect extends Effect {
    private List<Quadruple<String, Integer, Float, String>> mTags = new ArrayList<>();
    public TagToTargetEffect(JSONObject effect) {
        super(effect);

        String tag = effect.getString("tag");
        int duration = effect.getInt("duration");
        float chance = effect.getFloat("chance");
        String announcement = effect.getString("announcement");
        Quadruple<String, Integer, Float, String> data = new Quadruple<>(tag, duration, chance, announcement);
        mTags.add(data);
    }


    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {
        for (String targetTileID : targetTileIDs) {
            Entity entity = getEntityFromID(targetTileID);
            Tile tile = entity.get(Tile.class);
            String entityID = tile.getUnitID();
            Entity unitEntity = getEntityFromID(entityID);

            if (unitEntity == null) { continue; }
            tryApply(model, userID, entityID);

        }
        return false;
    }


    public void tryApply(GameModel model, String userID, String targetID) {

        for (Quadruple<String, Integer, Float, String> mTag : mTags) {

            String tag = mTag.getFirst();
            int duration = mTag.getSecond();
            float chance = mTag.getThird();
            String announcement = mTag.getFourth();;

            boolean success = passesChanceOutOf100(chance);
            if (!success) { continue; }

            if (!announcement.isBlank()) {
                announceWithFloatingTextCentered(model, announcement, targetID, ColorPalette.getRandomColor());
            }

            Entity target = getEntityFromID(targetID);
            StatisticsComponent statisticsComponent = target.get(StatisticsComponent.class);
            statisticsComponent.addTag(tag);
            List<Quadruple<String, String, Float, Integer>> statChanges = getKnownTag(tag);

            String id = UUID.randomUUID().toString();
            for (Quadruple<String, String, Float, Integer> entry : statChanges) {
                statisticsComponent.putMultiplicativeModification(
                        entry.getFirst(),
                        id,
                        entry.getSecond(),
                        entry.getThird(),
                        entry.getFourth()
                );
                System.out.println("UPDATED!");
            }
        }
    }

    public static List<Quadruple<String, String, Float, Integer>> getKnownTag(String tag) {

        List<Quadruple<String, String, Float, Integer>> statChanges = new ArrayList<>();
        Quadruple<String, String, Float, Integer> result = null;

        StringBuilder sb = new StringBuilder(tag);

        String tagLevelToken = sb.substring(sb.lastIndexOf("_") + 1);
        sb.delete(sb.lastIndexOf("_"), sb.length());

        String tagUpOrDownToken = sb.substring(sb.lastIndexOf("_") + 1);
        sb.delete(sb.lastIndexOf("_"), sb.length());

        String tagName = sb.toString();

        float tagLevel = (Float.parseFloat(tagLevelToken) * .25f );
        int tagLifetime = 2;

        if (tag.startsWith("defense")) {
            statChanges.add(new Quadruple<>("physical_defense", "the source", tagLevel, tagLifetime));
            statChanges.add(new Quadruple<>("magical_defense", "the source", tagLevel, tagLifetime));
        } else if (tag.startsWith("physical_defense")) {
            statChanges.add(new Quadruple<>("physical_defense", "the source", tagLevel, tagLifetime));
        }

        return statChanges;
    }
}
