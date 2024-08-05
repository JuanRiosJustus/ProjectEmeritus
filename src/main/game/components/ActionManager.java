package main.game.components;

import main.game.components.behaviors.UserBehavior;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.pools.ability.Ability;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager extends Component {

    public Entity targeting = null;
    public boolean mActed = false;
    public Ability mSelected = null;

    public final Set<Entity> mTargets = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mAreaOfEffect = ConcurrentHashMap.newKeySet();
    public final Set<Entity> mLineOfSight = ConcurrentHashMap.newKeySet();

    public void setAoe(Set<Entity> set) {
        mAreaOfEffect.clear();
        mAreaOfEffect.addAll(set);
    }

    public void setLos(Set<Entity> set) {
        mLineOfSight.clear();
        mLineOfSight.addAll(set);
    }

    public void setTargets(Set<Entity> set) {
        mTargets.clear();
        mTargets.addAll(set);
    }

    public void reset() {
        mAreaOfEffect.clear();
        mLineOfSight.clear();
        mTargets.clear();
        mActed = false;
        mSelected = null;
        previouslyTargeting = null;
    }
    public void setActed(boolean acted) {
        mActed = acted;
    }
    public Ability getSelected() { return mSelected; }
    private Entity previouslyTargeting = null;
    public boolean shouldNotUpdate(GameModel model, Entity targeting) {
        boolean isSameTarget = previouslyTargeting == targeting;
        if (!isSameTarget) {
//            System.out.println("Waiting for user action input... " + previouslyTargeting + " vs " + targeting);
        }
        previouslyTargeting = targeting;
        return isSameTarget && mOwner.get(UserBehavior.class) != null;
    }

    public void setSelected(Ability ability) {
        mSelected = ability;
    }
    public boolean hasActed() { return mActed; }
}
