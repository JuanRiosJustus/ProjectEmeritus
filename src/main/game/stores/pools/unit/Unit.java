package main.game.stores.pools.unit;

import java.util.*;

public class Unit {

    public final String name;
    public final String rarity;
    public final List<String> abilities = new ArrayList<>();
    public final Map<String, Integer> resources = new HashMap<>();
    public final Map<String, Integer> attributes = new HashMap<>();
    public final List<String> types = new ArrayList<>();

    public Unit(Map<String, String> dao) {
        name = dao.get("Name");
        rarity = dao.get("Rarity");

//        JsonArray array = (JsonArray) dao.get("stats.other.Types");
        types.addAll(Arrays.stream(dao.get("Type").split(" ")).toList());

//        array = (JsonArray) dao.get("stats.actions.Actives");
//        abilities.addAll(dao.get("Abilities"));

        String resource = "(resource)";
        String attribute = "(attribute)";
        for (Map.Entry<String, String> entry : dao.entrySet()) {
            String key = entry.getKey();

            if (key.contains(resource)) {
                resources.put(key.substring(0, key.indexOf(resource)), Integer.valueOf(entry.getValue()));
            } else if (key.contains(attribute)) {
                resources.put(key.substring(0, key.indexOf(attribute)), Integer.valueOf(entry.getValue()));
            }
        }
    }
}
