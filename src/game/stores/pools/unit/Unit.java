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
    public final Set<String> type;
    public final Set<String> abilities;

    public Unit(JsonObject dao) {
        name = dao.getString(Jsoner.mintJsonKey("name", null));
        unique = false;
        type = new HashSet<>(dao.getCollection(Jsoner.mintJsonKey("type", null)));
        health = dao.getInteger(Jsoner.mintJsonKey("health", null));
        energy = dao.getInteger(Jsoner.mintJsonKey("energy", null));
        physicalAttack = dao.getInteger(Jsoner.mintJsonKey("physicalAttack", null));
        physicalDefense = dao.getInteger(Jsoner.mintJsonKey("physicalDefense", null));
        magicalAttack = dao.getInteger(Jsoner.mintJsonKey("magicalAttack", null));
        magicalDefense = dao.getInteger(Jsoner.mintJsonKey("magicalDefense", null));
        speed = dao.getInteger(Jsoner.mintJsonKey("speed", null));
        climb = dao.getInteger(Jsoner.mintJsonKey("climb", null));
        move = dao.getInteger(Jsoner.mintJsonKey("move", null));
        abilities = new HashSet<>(dao.getCollection(Jsoner.mintJsonKey("abilities", null)));
    }
}
