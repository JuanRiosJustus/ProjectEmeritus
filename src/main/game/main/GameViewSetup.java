package main.game.main;

import main.ui.huds.controls.v1.MiniActionHUD;
import main.ui.huds.controls.v1.MiniMovementHUD;
import main.ui.huds.controls.v1.MiniOtherHUD;
import main.ui.huds.controls.v1.MiniSummaryHUD;
import main.ui.huds.controls.v2.ActionHUD;

public class GameViewSetup {

    public static MiniActionHUD createActionHUD(int width, int height) {
        MiniActionHUD miniActionHUD = new MiniActionHUD(width, height);
        miniActionHUD.getEnterButton().setFont(miniActionHUD.getEnterButton().getFont().deriveFont(30f));
        miniActionHUD.setVisible(false);
        return miniActionHUD;
    }

    public static MiniSummaryHUD createMiniSummaryHUD(int width, int height) {
        MiniSummaryHUD miniSummaryHUD = new MiniSummaryHUD(width, height);
        miniSummaryHUD.getEnterButton().setFont(miniSummaryHUD.getEnterButton().getFont().deriveFont(30f));
        miniSummaryHUD.setVisible(false);
        return miniSummaryHUD;
    }

    public static ActionHUD createActionHUD2(int width, int height) {
        ActionHUD hud = new ActionHUD(width, height);
        hud.getEnterButton().setFont(hud.getEnterButton().getFont().deriveFont(30f));
        hud.setVisible(false);
        return hud;
    }

    public static MiniMovementHUD createMovementHUD(int width, int height) {
        MiniMovementHUD movementPanel = new MiniMovementHUD(width, height);
        movementPanel.getEnterButton().setFont(movementPanel.getEnterButton().getFont().deriveFont(30f));
        movementPanel.setVisible(false);
        return movementPanel;
    }

    public static MiniOtherHUD createOtherHUD(int width, int height) {
        MiniOtherHUD miniOtherHUD = new MiniOtherHUD(width, height);
        miniOtherHUD.getEnterButton().setFont(miniOtherHUD.getEnterButton().getFont().deriveFont(30f));
        miniOtherHUD.setVisible(false);
        return miniOtherHUD;
    }
}
