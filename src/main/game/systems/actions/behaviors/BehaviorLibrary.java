package main.game.systems.actions.behaviors;

import com.alibaba.fastjson2.JSONArray;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.main.GameState;

import java.util.ArrayList;
import java.util.List;

public class BehaviorLibrary {

    private int mUnitDefaultVisionRange = 9;
    public List<String> getAllEnemyUnits(GameModel model, String activeUnitEntityID) {

        JSONArray allEnemyEntities = model.getAllEnemyEntities(activeUnitEntityID);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < allEnemyEntities.size(); i++) {
            String enemyEntity = allEnemyEntities.getString(i);
            result.add(enemyEntity);
        }
        return result;
    }
}
