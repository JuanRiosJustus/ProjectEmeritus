import engine.Engine;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.AssetPool;
import game.stores.pools.UnitPool;
import ui.screen.GamePanel;

public class Main {

    public static void main(String[] args) {
        AbilityPool.instance();
        AssetPool.instance();
        UnitPool.instance();
        AbilityPool.instance();
        Engine.get().run();


//        SecondTimer st = new SecondTimer();
//        Thread.sleep(2519);
//        System.out.println(st.elapsed());
    }
}
