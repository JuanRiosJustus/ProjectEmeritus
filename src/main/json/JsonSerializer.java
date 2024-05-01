package main.json;

import com.github.cliftonlabs.json_simple.JsonObject;
import main.game.components.Identity;
import main.game.components.Statistics;
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
        Statistics statistics = entity.get(Statistics.class);
        Identity identity = entity.get(Identity.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("name", identity.getName());
        jsonObject.put("uuid", identity.getUuid());
        jsonObject.put("row", row);
        jsonObject.put("column", column);
        jsonObject.put("level", statistics.getLevel());
        jsonObject.put("experience", statistics.getExperience());

        return jsonObject;
    }
}
