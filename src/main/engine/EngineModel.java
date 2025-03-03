package main.engine;

public class EngineModel {

    private EngineScene mEngineScene;
    public void update() { if (mEngineScene == null) { return; } mEngineScene.update(); }
    public void input() { if (mEngineScene == null) { return; } mEngineScene.input(); }

    public void stage(EngineScene engineScene) { mEngineScene = engineScene; }
}
