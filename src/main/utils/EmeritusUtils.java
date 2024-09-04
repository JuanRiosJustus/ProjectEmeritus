package main.utils;

import main.constants.Constants;
import main.game.stores.pools.action.Action;

public class EmeritusUtils {

    private static final String physicalType = "Slash Pierce Blunt";
    private static final String magicalType = "Light Water Dark Fire Earth";

    public static String getAbbreviation(String key) {
        String abbreviation = "";
        switch (key) {
            case Constants.PHYSICAL_ATTACK -> abbreviation = "PhyAtk";
            case Constants.PHYSICAL_DEFENSE -> abbreviation = "PhyDef";
            case Constants.MAGICAL_ATTACK -> abbreviation = "MgcAtk";
            case Constants.MAGICAL_DEFENSE -> abbreviation = "MgcDef";
            case Constants.HEALTH -> abbreviation = "HP";
            case Constants.ENERGY -> abbreviation = "NRG";
            default ->  System.err.println("Unknown abbreviation " + key);
        }
        return abbreviation;
    }

//    public static void get

    public static String getAbilityTypes(Action action) {
        // If the ability is normal, prioritize showing that type
        if (action.getTypes().contains("Normal") || action.getTypes().contains("normal") ||
            action.getTypes().contains("Physical") || action.getTypes().contains("physical")) {
            return action.impact;
        } else {
            return action.getTypes().stream().iterator().next();
        }
    }

    public static boolean isMagicalType(Action action) {
        return action.getTypes().stream().anyMatch(magicalType::contains);
    }
    public static boolean isPhysicalType(Action action) {
        return action.getTypes().stream().anyMatch(physicalType::contains);
    }
}
