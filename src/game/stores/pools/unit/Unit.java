package game.stores.pools.unit;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.util.HashSet;
import java.util.Set;

public class Unit {
    public final String name;
    public final boolean unique;
    public final Set<String> types;
    public final int health;
    public final int energy;
    public final int physicalAttack;
    public final int physicalDefense;
    public final int magicalAttack;
    public final int magicalDefense;
    public final int speed;
    public final int jump;
    public final int move;
    public final Set<String> abilities;
    public Unit(JsonObject jsonObject) {
        name = jsonObject.getString(Jsoner.mintJsonKey("name", null));
        types = new HashSet<>(jsonObject.getCollection(Jsoner.mintJsonKey("type", null)));
        unique = jsonObject.getBoolean(Jsoner.mintJsonKey("unique", null));
        health = jsonObject.getInteger(Jsoner.mintJsonKey("health", null));
        energy = jsonObject.getInteger(Jsoner.mintJsonKey("energy", null));
        physicalAttack = jsonObject.getInteger(Jsoner.mintJsonKey("physicalAttack", null));
        physicalDefense = jsonObject.getInteger(Jsoner.mintJsonKey("physicalDefense", null));
        magicalAttack = jsonObject.getInteger(Jsoner.mintJsonKey("magicalAttack", null));
        magicalDefense = jsonObject.getInteger(Jsoner.mintJsonKey("magicalDefense", null));
        speed = jsonObject.getInteger(Jsoner.mintJsonKey("speed", null));
        move = jsonObject.getInteger(Jsoner.mintJsonKey("move", null));
        jump = jsonObject.getInteger(Jsoner.mintJsonKey("jump", null));
        abilities = new HashSet<>(jsonObject.getCollection(Jsoner.mintJsonKey("abilities", null)));
    }
}
