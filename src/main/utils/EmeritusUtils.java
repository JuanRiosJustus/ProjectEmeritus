package main.utils;

import main.constants.Constants;

public class EmeritusUtils {

    private static final String physicalType = "Slash Pierce Blunt";
    private static final String magicalType = "Light Water Dark Fire Earth";

    private static final String LEVEL = "level";
    private static final String STAMINA = "stamina";
    private static final String HEALTH = "health";
    private static final String MANA = "mana";
    private static final String EXPERIENCE = "experience";

    private static final String PHYSICAL_DEFENSE = "physical_defense";
    private static final String PHYSICAL_ATTACK = "physical_attack";
    private static final String MAGICAL_DEFENSE = "magical_defense";
    private static final String MAGICAL_ATTACK = "magical_attack";

    private static final String JUMP = "jump";
    private static final String SPEED = "speed";
    private static final String MOVE = "move";
    private static final String CLIMB = "climb";

    public static String getAbbreviation(String key) {
        String abbreviation = "";
        switch (key) {
            case MOVE -> abbreviation = "MV";
            case JUMP -> abbreviation = "JP";
            case SPEED -> abbreviation = "SD";
            case CLIMB -> abbreviation = "CM";

            case LEVEL -> abbreviation = "LV";

            case STAMINA -> abbreviation = "SP";
            case HEALTH -> abbreviation = "HP";
            case MANA -> abbreviation = "MP";
            case EXPERIENCE -> abbreviation = "XP";

            case PHYSICAL_ATTACK -> abbreviation = "PA";
            case PHYSICAL_DEFENSE -> abbreviation = "PD";
            case MAGICAL_ATTACK -> abbreviation = "MA";
            case MAGICAL_DEFENSE -> abbreviation = "MD";

            default ->  System.err.println("Unknown abbreviation " + key);
        }
        return abbreviation;
    }

//    public static void get

//    public static String getAbilityTypes(Action action) {
//        // If the ability is normal, prioritize showing that type
//        if (action.getTypes().contains("Normal") || action.getTypes().contains("normal") ||
//            action.getTypes().contains("Physical") || action.getTypes().contains("physical")) {
//            return action.impact;
//        } else {
//            return action.getTypes().stream().iterator().next();
//        }
//    }

//    public static boolean isMagicalType(Action action) {
//        return action.getTypes().stream().anyMatch(magicalType::contains);
//    }
//    public static boolean isPhysicalType(Action action) {
//        return action.getTypes().stream().anyMatch(physicalType::contains);
//    }
}
