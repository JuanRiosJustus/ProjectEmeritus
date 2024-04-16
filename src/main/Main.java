package main;

import main.constants.Settings;
import main.engine.Engine;
import main.game.main.GameController;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.ability.AbilityPool;
import main.game.stores.pools.unit.UnitPool;
//import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.logging.ELoggerManager;
import main.logging.ELogger;
import main.ouput.UserSave;
import main.ui.presets.loadout.LoadOutScene;

import javax.swing.UIManager;

public class Main {

        
    public static void main(String[] args) throws Exception {

//        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
//        for (UIManager.LookAndFeelInfo look : looks) {
//            System.out.println(look.getClassName());
//        }

//        UIManager.put("ProgressBar.repaintInterval", 100);
//        UIManager.put("ProgressBar.border", com.formdev.flatlaf.themes.FlatMacDarkLaf
//                BorderFactory.createLineBorder(Color.blue, 2));

        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//         UserSavedData.getInstance().load(Constants.USER_SAVE_DIRECTORY + "ExampleSaveData.json");
//         UserSavedData.getInstance().createOrRead("TestFilePath.json");
//         UserSavedData.getInstance().update();

        // Loads the resources before game has started
//        Engine.getInstance();
        AssetPool.getInstance();
        FontPool.getInstance();
        AbilityPool.getInstance();
        UnitPool.getInstance();
        GameController.getInstance();
        UserSave.getInstance();

        ELogger eLogger = ELoggerFactory.getInstance().getELogger(Main.class);
        eLogger.setLogLevel(ELoggerManager.LOG_LEVEL_WARN);

//        UIManager.put("ComboBox.background", new ColorUIResource(ColorPalette.getRandomColor()));


        int width = Settings.getInstance().getInteger(Settings.DISPLAY_WIDTH);
        int height = Settings.getInstance().getInteger(Settings.DISPLAY_HEIGHT) - Engine.getInstance().getHeaderSize();

        // // SceneManager.instance().set(SceneManager.GAME_SCENE);
        // SceneManager.getInstance().set(SceneManager.MAIN_MENU_SCENE);
        // Engine.getInstance().controller.view.
        // SceneManager.instance().set(SceneManager.EDITOR_SCENE);

//        var r = new PreGamePanel(width, height);
//        var r  = new MenuScene(width, height);

//        var r = new EditorScene(width, height);
//        Engine.getInstance().getController().stage(r);

//        Engine.getInstance().getController().stage(new MenuScene(width, height));
//        Engine.getInstance().getController().stage(new EditorScene(width, height));

//        Engine.getInstance().getController().stage(GameController.getInstance());
//        GameController.getInstance().getModel().run();
//
        Engine.getInstance().getController().stage(new LoadOutScene(width, height));

        Engine.getInstance().run();
    }
}
