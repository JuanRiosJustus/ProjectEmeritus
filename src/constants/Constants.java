package constants;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;

public class Constants {

    public static final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

    public static final int APPLICATION_WIDTH = size.width;
    public static final int APPLICATION_HEIGHT = size.height;
//    public static final int APPLICATION_WIDTH = 1280;
//    public static final int APPLICATION_HEIGHT = 720;
    public static final String APPLICATION_NAME = "Project Emeritus v.03";

    public static final String START_BUTTON = "Start";
    public static final String CONTINUE_BUTTON = "Continue";
    public static final String EDIT_BUTTON = "Edit";
    public static final String EXIT_BUTTON = "Exit";
    public static final String SETTINGS_BUTTON = "Settings";

    public static final int CURRENT_SPRITE_SIZE = 64;
    public static final int BASE_SPRITE_SIZE = 64;

    public static final int SIDE_BAR_WIDTH = (int) (APPLICATION_WIDTH * .2);
    public static final int SIDE_BAR_ACTIONS_HEIGHT = (int) (APPLICATION_HEIGHT * .2);
    public static final int SIDE_BAR_LOGS_HEIGHT = (int) (APPLICATION_HEIGHT * .25);
    public static final int SIDE_BAR_MAIN_PANEL_HEIGHT = (int) (APPLICATION_HEIGHT * .5);

    public static final String FONT_FILEPATH = file("./res/data/MAKISUPA.ttf");
    public static final String WALLS_SPRITESHEET_FILEPATH = file("./res/graphics/walls.png");
    public static final String FLOORS_SPRITESHEET_FILEPATH = file("./res/graphics/floors.png");
    public static final String TERRAIN_SPRITESHEET_FILEPATH = file("./res/graphics/terrains2.png");
    public static final String STRUCTURE_SPRITESHEET_FILEPATH = file("./res/graphics/structures.png");
    public static final String SPECIAL_SPRITESHEET_FILEPATH = file("./res/graphics/special.png");
    public static final String SHADOWS_SPRITESHEET_FILEPATH = file("./res/graphics/shadows.png");
    public static final String TEST_MAP = file("./res/testMap.txt");

    public static final String ABILITY_DATA_FILE = file("./res/abilities/Data.csv");
    public static final String ABILITIES_SPRITESHEET_FILEPATH = file("./res/abilities/graphics/");

    public static final String UNITS_DATA_FILE = file("./res/units/Data.csv");
    public static final String UNITS_SPRITESHEET_FILEPATH = file("./res/units/graphics/");

    /*
     *    ██████╗███████╗██╗   ██╗    ██████╗ ██████╗  ██████╗ ██████╗ ███████╗██████╗ ████████╗██╗███████╗███████╗
     *   ██╔════╝██╔════╝██║   ██║    ██╔══██╗██╔══██╗██╔═══██╗██╔══██╗██╔════╝██╔══██╗╚══██╔══╝██║██╔════╝██╔════╝
     *   ██║     ███████╗██║   ██║    ██████╔╝██████╔╝██║   ██║██████╔╝█████╗  ██████╔╝   ██║   ██║█████╗  ███████╗
     *   ██║     ╚════██║╚██╗ ██╔╝    ██╔═══╝ ██╔══██╗██║   ██║██╔═══╝ ██╔══╝  ██╔══██╗   ██║   ██║██╔══╝  ╚════██║
     *   ╚██████╗███████║ ╚████╔╝     ██║     ██║  ██║╚██████╔╝██║     ███████╗██║  ██║   ██║   ██║███████╗███████║
     *    ╚═════╝╚══════╝  ╚═══╝      ╚═╝     ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝╚══════╝╚══════╝
     */

    public static final String TAGS = "Tags";
    public static final String SPEED = "Speed";
    public static final String DISTANCE = "Distance";
    public static final String ABILITIES = "Abilities";
    public static final String HEALTH = "Health";
    public static final String ENERGY = "Energy";

    public static final String AREA_OF_EFFECT = "AreaOfEffect";
    public static final String RANGE = "Range";
    public static final String NAME = "Name";
    public static final String ACCURACY = "Accuracy";
    public static final String TYPE = "Type";

    public static final String MISSING = "Missing";
    public static final String CURRENT = "Current";
    public static final String MAX = "Max";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";
    public static final String FLAT = "Flat";
    public static final String PERCENT = "Percent";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String PHYSICAL_ATTACK = "PhysicalAttack";
    public static final String PHYSICAL_DEFENSE = "PhysicalDefense";
    public static final String MAGICAL_ATTACK = "MagicalAttack";
    public static final String MAGICAL_DEFENSE = "MagicalDefense";
    public static final String PA = "PA";
    public static final String PD = "PD";
    public static final String MA = "MA";
    public static final String MD = "MD";
    public static final String DESCRIPTION = "Description";
    public static final String STATUS = "Status";
    public static final String CAN_HIT_USER = "CanHitUser";
    public static final String LIGHT = "Light";
    public static final String WATER = "Water";
    public static final String DARK = "Dark";
    public static final String FIRE = "Fire";
    public static final String EARTH = "Earth";
    public static final String NORMAL = "Normal";
    public static final String PIERCE = "Pierce";
    public static final String SLASH = "Slash";
    public static final String BLUNT = "Blunt";

    public static final String NEGATE = "Negate";

    public static final String ABILITY_UI_SHOWING = "ability.ui.showing";
    public static final String ABILITY_UI_SELECTEDABILITIY = "ability.ui.selectedAbility";
    public static final String MOVEMENT_UI_SHOWING = "movement.ui.showing";
    public static final String SETTINGS_UI_SHOWING = "settings.ui.showing";
    public static final String SETTINGS_UI_AUTOENDTURNS = "setting.ui.autoEndTurns";
    public static final String SETTINGS_UI_FASTFORWARDTURNS = "settings.ui.fastForwardTurns";
    public static final String ACTIONS_UI_ENDTURN = "actions.ui.endTurn";
    public static final String MAIN_MENU_SCENE = "MainMenuScene";
    public static final String GAME_SCENE = "GameScene";
    public static final String EDIT_SCENE = "EditScene";
    public static final String MAIN_MENU = "Main Menu";

    public static String file(String str) {
        return str.replaceAll("\"", File.separator);
    }
}
