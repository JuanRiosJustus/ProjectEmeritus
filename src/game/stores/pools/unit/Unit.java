package game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Unit {

    public final String name;
    public final boolean unique;
    public final int health;
    public final int energy;
    public final int physicalAttack;
    public final int physicalDefense;
    public final int magicalAttack;
    public final int magicalDefense;
    public final int speed;
    public final int climb;
    public final int move;
    public final Set<String> types;
    public final Set<String> abilities;

    public Unit(Map<String, String> row) {
        name = row.get("name");
        unique = false;
        types = new HashSet<>(Arrays.asList(row.get("types").split("\\|")));
        health = Integer.parseInt(row.get("health"));
        energy = Integer.parseInt(row.get("energy"));
        physicalAttack = Integer.parseInt(row.get("physical_attack"));
        physicalDefense = Integer.parseInt(row.get("physical_defense"));
        magicalAttack = Integer.parseInt(row.get("magical_attack"));
        magicalDefense = Integer.parseInt(row.get("magical_defense"));
        speed = Integer.parseInt(row.get("speed"));
        climb = Integer.parseInt(row.get("climb"));
        move = Integer.parseInt(row.get("move"));
        abilities = new HashSet<>(Arrays.asList(row.get("abilities").split("\\|")));
        // unique = 
    }
}
