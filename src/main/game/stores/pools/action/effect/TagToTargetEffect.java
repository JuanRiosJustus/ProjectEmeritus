package main.game.stores.pools.action.effect;

import main.constants.Quadruple;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagToTargetEffect extends Effect {
    private List<Quadruple<String, Integer, Float, String>> mTags = new ArrayList<>();
    public TagToTargetEffect(JSONObject effect) {
        super(effect);

        JSONArray tagsData = effect.getJSONArray("tags");
        for (int index = 0; index < tagsData.length(); index++) {
            JSONObject tagData = tagsData.getJSONObject(index);
            String tag = tagData.getString("tag");
            int duration = tagData.getInt("duration");
            float chance = tagData.getFloat("chance");
            String announcement = tagData.getString("announcement");
            Quadruple<String, Integer, Float, String> data = new Quadruple<>(tag, duration, chance, announcement);
            mTags.add(data);
        }
    }

    @Override
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {

        for (Entity entity : targets) {
            Tile tile = entity.get(Tile.class);
            Entity unitEntity = tile.getUnit();

            if (unitEntity == null) { continue; }
            tryApply(model, null, unitEntity);

        }
        return false;
    }

    public void tryApply(GameModel model, Entity user, Entity target) {

        for (Quadruple<String, Integer, Float, String> mTag : mTags) {

            String tag = mTag.getFirst();
            int duration = mTag.getSecond();
            float chance = mTag.getThird();
            String announcement = mTag.getFourth();;

            boolean success = passesChanceOutOf100(chance);
            if (!success) { continue; }

            if (!announcement.isBlank()) {
                announceWithFloatingTextCentered(model, announcement, target, ColorPalette.getRandomColor());
            }

            System.out.println(
                    "ADD TAG TO TAGRET HERE"
            );

        }
    }
}
