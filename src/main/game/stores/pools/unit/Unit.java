package main.game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Unit {

    public final String name;
    public final String rarity;
    public final Set<String> type;
    public final Set<String> abilities;
    public final Set<String> passives;
    public final Map<String, Integer> stats;

    public Unit(JsonObject dao) {
        name = (String) dao.get("Unit");
        rarity = (String) dao.get("Rarity");

        stats = new HashMap<>();
        for (Map.Entry<String, Object> entry : dao.entrySet()) {
            if (!(entry.getValue() instanceof BigDecimal value)) { continue; }
            stats.put(entry.getKey(), value.intValue());
        }

        JsonArray array = (JsonArray) dao.get("Abilities");
        abilities = array.stream().map(Object::toString).collect(Collectors.toSet());

        array = (JsonArray) dao.get("Passives");
        passives = array.stream().map(Object::toString).collect(Collectors.toSet());

        array = (JsonArray) dao.get("Types");
        type = array.stream().map(Object::toString).collect(Collectors.toSet());
    }

    public Unit(Map<String, String> dao) {
        name = dao.get("Unit");
        rarity = dao.get("Rarity");

        stats = new HashMap<>();
        for (Map.Entry<String, String> entry : dao.entrySet()) {
            boolean isNumericalEntry = isInteger(entry.getValue());
            if (!isNumericalEntry) { continue; }
            stats.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        }

        List<String> sanitized = Arrays.asList(dao.get("Types").split(","));
        type = new HashSet<>(sanitized.stream().map(String::trim).toList());

        sanitized = Arrays.asList(dao.get("Passives").split(","));
        passives = new HashSet<>(sanitized.stream().map(String::trim).toList());

        sanitized = Arrays.asList(dao.get("Abilities").split(","));
        abilities = new HashSet<>(sanitized.stream().map(String::trim).toList());
    }

    private static boolean isInteger(String str) {
        try {
            int value = Integer.parseInt(str);
            return true;
        } catch (Exception ignored) { }
        return false;
    }
}
