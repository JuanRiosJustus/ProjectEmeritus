package main.utils;

import main.constants.Constants;
import main.game.stores.pools.ability.Ability;

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

    public static String getAbilityTypes(Ability ability) {
        // If the ability is normal, prioritize showing that type
        if (ability.getTypes().contains("Normal") || ability.getTypes().contains("normal") ||
            ability.getTypes().contains("Physical") || ability.getTypes().contains("physical")) {
            return ability.impact;
        } else {
            return ability.getTypes().stream().iterator().next();
        }
    }

    public static boolean isMagicalType(Ability ability) {
        return ability.getTypes().stream().anyMatch(magicalType::contains);
    }
    public static boolean isPhysicalType(Ability ability) {
        return ability.getTypes().stream().anyMatch(physicalType::contains);
    }
}
