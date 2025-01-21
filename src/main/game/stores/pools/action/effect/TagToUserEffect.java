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

public class TagToUserEffect extends TagToTargetEffect {
    public TagToUserEffect(JSONObject effect) {
        super(effect);

        JSONArray tagsData = effect.getJSONArray("tags");
        for (int index = 0; index < tagsData.length(); index++) {
            JSONObject tagData = tagsData.getJSONObject(index);
            String tag = tagData.getString("tag");
            int duration = tagData.getInt("duration");
            float chance = tagData.getFloat("chance");
            String announcement = tagData.getString("announcement");
            Quadruple<String, Integer, Float, String> data = new Quadruple<>(tag, duration, chance, announcement);
            List<Quadruple<String, Integer, Float, String>> mTags = new ArrayList<>();
            mTags.add(data);
        }
    }

    @Override
    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {

        tryApply(model, null, user);

        return false;
    }
}
