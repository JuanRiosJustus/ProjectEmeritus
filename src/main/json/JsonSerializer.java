package main.json;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.IdentityComponent;
import main.game.components.StatisticsComponent;
import main.game.entity.Entity;

public class JsonSerializer {

    /**
     * Converts the unit to json
     * @param entity
     * @param row
     * @param column
     * @return
     */
    public JsonObject convertUnitForPlacementToJson(Entity entity, int row, int column) {
        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        IdentityComponent identityComponent = entity.get(IdentityComponent.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("name", identityComponent.getName());
        jsonObject.put("uuid", identityComponent.getUuid());
        jsonObject.put("row", row);
        jsonObject.put("column", column);
        jsonObject.put("level", statisticsComponent.getLevel());
        jsonObject.put("experience", statisticsComponent.getExperience());

        return jsonObject;
    }
}
