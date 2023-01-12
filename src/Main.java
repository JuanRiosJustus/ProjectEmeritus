import engine.Engine;
import ui.presets.SceneManager;

import javax.swing.SwingUtilities;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static void main(String[] args) {

        // Create UI elements
//        SceneManager.instance();

//        // Initialize Resources
//        AbilityPool.instance();
//        AssetPool.instance();
//        UnitPool.instance();
//        AbilityPool.instance();

//        GameController.instance();

//        Engine.instance();
//        Engine.instance.controller.view.setScene(SceneManager.instance.getScene(Constants.GAME_SCENE));
//        Engine.instance.controller.view.setScene(SceneManager.instance.getScene(Constants.MAIN_MENU_SCENE));

//        System.out.println(EventQueue.isDispatchThread());
//        SceneManager.instance.setScene(Constants.GAME_SCENE);

//        try {
//            EventQueue.invokeAndWait(() -> {
//                SceneManager.instance().set(SceneManager.MAIN_MENU_SCENE);
//
//
//                Engine.instance().run();
//            });
//        } catch (Exception e) {
//            System.out.println("Error");
//        }

        SceneManager.instance().set(SceneManager.MAIN_MENU_SCENE);
        Engine.instance().run();

//        SwingUtilities.invokeAndWait(() -> {
//            SceneManager.instance().set(SceneManager.MAIN_MENU_SCENE);
//            Engine.instance().run();
//        });
//


//        Application application = new Application();
//        SecondTimer st = new SecondTimer();
//        Thread.sleep(2519);
//        System.out.println(st.elapsed());
    }
}
