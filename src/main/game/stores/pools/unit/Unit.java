package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Unit {

    public final String name;
    public final String rarity;
    public final List<String> abilities = new ArrayList<>();
    public final Map<String, Integer> resources = new HashMap<>();
    public final Map<String, Integer> attributes = new HashMap<>();
    public final List<String> types = new ArrayList<>();

    public Unit(JsonObject dao) {
        name = (String) dao.get("Stats.Other.Unit");
        rarity = (String) dao.get("Stats.Other.Rarity");

        JsonArray array = (JsonArray) dao.get("Stats.Other.Types");
        types.addAll(array.stream().map(Object::toString).toList());

        array = (JsonArray) dao.get("Stats.Traits.Actives");
        abilities.addAll(array.stream().map(Object::toString).toList());
//
//        stats = new HashMap<>();
//        for (Map.Entry<String, Object> entry : dao.entrySet()) {
//            if (!(entry.getValue() instanceof BigDecimal value)) { continue; }
//            stats.put(entry.getKey(), value.intValue());
//        }
//
//        JsonArray array = (JsonArray) dao.get("Actives");
//        abilities = array.stream().map(Object::toString).collect(Collectors.toSet());
//
//        array = (JsonArray) dao.get("Passives");
//        passives = array.stream().map(Object::toString).collect(Collectors.toSet());
//
//        array = (JsonArray) dao.get("Types");
//        type = array.stream().map(Object::toString).collect(Collectors.toSet());

        for (Map.Entry<String, Object> entry : dao.entrySet()) {
            String key = entry.getKey();
            if (key.contains("Stats.Resource")) {
                BigDecimal value = (BigDecimal) entry.getValue();
                resources.put(key, value.intValue());
            } else if (key.contains("Stats.Attribute")) {
                BigDecimal value = (BigDecimal) entry.getValue();
                attributes.put(key, value.intValue());
            }
        }
    }
}
