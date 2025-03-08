package main.game.stores.pools.action.effect;

import main.constants.Quadruple;
import main.game.main.GameModel;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagToUserEffect extends TagToTargetEffect {
    public TagToUserEffect(JSONObject effect) {
        super(effect);

        String tag = effect.getString("tag");
        int duration = effect.getInt("duration");
        float chance = effect.getFloat("chance");
        String announcement = effect.getString("announcement");
        Quadruple<String, Integer, Float, String> data = new Quadruple<>(tag, duration, chance, announcement);
        List<Quadruple<String, Integer, Float, String>> mTags = new ArrayList<>();
        mTags.add(data);
    }

//    @Override
//    public boolean apply(GameModel model, Entity user, Set<Entity> targets) {
//
//        tryApply(model, null, user);
//
//        return false;
//    }

    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {

        tryApply(model, null, userID);

        return false;
    }
}
