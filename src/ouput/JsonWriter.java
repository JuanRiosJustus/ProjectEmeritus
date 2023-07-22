package ouput;

import java.io.FileWriter;
import java.io.PrintWriter;

import com.github.cliftonlabs.json_simple.JsonObject;

import game.components.Statistics;
import game.entity.Entity;
import game.stats.node.StatsNode;

public class JsonWriter {
        
    public static void saveUnit(String path, Entity unit) {
        try {
            JsonObject unitData = new JsonObject();

            // Write the name fo the character
            String name = unit.get(Statistics.class).getName();
            unitData.put("name", name);

            // Write the types of the characcter
            String type = unit.get(Statistics.class).getTypes().toString();
            unitData.put("type", type);

            Statistics stats = unit.get(Statistics.class);
            for (String nodeName : stats.getKeySet()) {
                StatsNode node = stats.getStatsNode(nodeName);
                unitData.put(nodeName, node.getBase());
            }

            String fileName = unit.toString() + ".json";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(unitData.toJson());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
