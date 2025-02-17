package main.game.stores.pools.action.effect;

import main.game.entity.Entity;
import main.game.main.GameModel;
import org.json.JSONObject;

import java.awt.Color;
import java.util.Set;

public class AnnounceEffect extends Effect {
    private String mAnnouncement = null;

    public AnnounceEffect(JSONObject effect) {
        super(effect);
        mAnnouncement = effect.getString("announcement");
    }

    @Override
    public boolean apply(GameModel model, String userID, Set<String> targetTileIDs) {

        announceWithFloatingTextCentered(model, mAnnouncement, userID, Color.BLUE);

        return false;
    }
}
