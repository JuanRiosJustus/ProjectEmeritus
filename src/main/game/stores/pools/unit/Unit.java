package main.game.stores.pools.unit;

import java.util.*;

public class Unit {

    public final String species;
    public final String rarity;
    public final Set<String> type;
    public final Set<String> abilities;
    public final Set<String> passives;
    public final Map<String, Integer> stats;

    public Unit(Map<String, String> dao) {
        species = dao.get("Unit");
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
