package main.game.stores.pools.action.effect;

import main.constants.Quadruple;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.JsonDatabase;
import main.game.stores.JsonTable;
import main.game.stores.pools.ColorPaletteV1;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TagToTargetEffect extends Effect {
    protected List<JSONObject> mTags = new ArrayList<>();
    public TagToTargetEffect(JSONObject effect) {
        super(effect);

        JSONArray tags = effect.getJSONArray("tags");

        JsonTable tagTable = JsonDatabase.getInstance().get("tags");

        for (int index = 0; index < tags.length(); index++) {
            String tag = tags.getString(index);
            JSONObject tagData = tagTable.getRow(tag);
            if (tagData == null) { continue; }
            mTags.add(tagData);
        }
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
        mLogger.info("Started applying effects to target");
        for (JSONObject tagData : mTags) {
            String id = tagData.getString("id");
            int duration = tagData.getInt("duration");
            float chance = tagData.getFloat("chance");
            String announcement = tagData.getString("announcement");
            JSONArray modifications = tagData.getJSONArray("statistic_modifications");

            boolean success = passesChanceOutOf100(chance);
            if (!success) {
                mLogger.info("{} Tag did not get applied", id);
                continue;
            }

            if (!announcement.isBlank()) {
                announceWithFloatingTextCentered(model, announcement, targetID, ColorPaletteV1.getRandomColor());
            }

            Entity target = getEntityFromID(targetID);
            StatisticsComponent statisticsComponent = target.get(StatisticsComponent.class);

            statisticsComponent.addTag(UUID.randomUUID().toString(), id, duration);

            for (int index = 0; index < modifications.length(); index++) {
                JSONObject tagBuffOrDebuffData = modifications.getJSONObject(index);
                String attribute = tagBuffOrDebuffData.getString("attribute");
                String modification = tagBuffOrDebuffData.getString("modification");
                float value = tagBuffOrDebuffData.getFloat("value");
                statisticsComponent.putModification(attribute, modification, id, value, duration);
                mLogger.info(
                        "{} Tag has applied a stat modifier ({},{},{})",
                        id, attribute, modification, value
                );
            }
        }

        mLogger.info("Finished applying effects to target");
    }
}
