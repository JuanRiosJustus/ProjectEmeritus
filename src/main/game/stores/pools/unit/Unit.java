package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonObject;

import java.math.BigDecimal;
import java.util.*;

public class Unit {

    public final String name;
    public final String rarity;
    public final List<String> abilities = new ArrayList<>();
    public final Map<String, Integer> resources = new HashMap<>();
    public final Map<String, Integer> attributes = new HashMap<>();
    public final List<String> types = new ArrayList<>();
    public final String named;

    public Unit(JsonObject dao) {
        name = (String) dao.get("Name");
        rarity = (String) dao.get("Rarity");
        named = (String) dao.getOrDefault("Named", "");

        String temp = (String) dao.get("Type");
        types.addAll(List.of(temp.split(",")));

        temp = (String) dao.get("Abilities");
        abilities.addAll(List.of(temp.split(",")));

        String resource = "(resource)";
        String attribute = "(attribute)";
        for (String key : dao.keySet()) {
            if (key.contains(resource)) {
                BigDecimal value = (BigDecimal) dao.get(key);
                resources.put(key.substring(0, key.indexOf(resource)), value.intValue());
            } else if (key.contains(attribute)) {
                BigDecimal value = (BigDecimal) dao.get(key);
                resources.put(key.substring(0, key.indexOf(attribute)), value.intValue());
            }
        }
    }
}
