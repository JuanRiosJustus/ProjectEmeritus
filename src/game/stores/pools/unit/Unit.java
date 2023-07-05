package game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Unit {

    public final String name;
    public final String rarity;
    public final int health;
    public final int energy;
    public final int physicalAttack;
    public final int physicalDefense;
    public final int magicalAttack;
    public final int magicalDefense;
    public final int speed;
    public final int climb;
    public final int move;
    public final Set<String> type;
    public final Set<String> abilities;


    public Unit(Map<String, String> dao) {
        name = dao.get("Name");
        rarity = dao.get("Rarity");
        type = new HashSet<>(Arrays.asList(dao.get("Type").split(",")));
        health = Integer.parseInt(dao.get("Health"));
        energy = Integer.parseInt(dao.get("Energy"));
        physicalAttack = Integer.parseInt(dao.get("PhysicalAttack"));
        physicalDefense = Integer.parseInt(dao.get("PhysicalDefense"));
        magicalAttack = Integer.parseInt(dao.get("MagicalAttack"));
        magicalDefense = Integer.parseInt(dao.get("MagicalDefense"));
        speed = Integer.parseInt(dao.get("Speed"));
        climb = Integer.parseInt(dao.get("Climb"));
        move = Integer.parseInt(dao.get("Move"));
        List<String> sanitized = Arrays.asList(dao.get("Abilities").split(","));
        abilities = new HashSet<>(sanitized.stream().map(e -> e.trim()).toList());
    }
}
