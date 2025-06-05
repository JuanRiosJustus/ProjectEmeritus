package main.ui.scenes;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import main.engine.EngineRunnable;

public class DeploymentScene extends EngineRunnable {

    public DeploymentScene(int width, int height) { super(width, height); }
    @Override
    public Scene render() {
        StackPane sp = new StackPane();


        sp.getChildren().add(new Button("tooooooo"));



        return new Scene(sp, mWidth, mHeight);
    }
}
