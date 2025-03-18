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
    }

    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {

        tryApply(model, null, userID);

        return false;
    }
}
