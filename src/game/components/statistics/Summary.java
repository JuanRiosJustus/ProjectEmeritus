package game.components.statistics;

import constants.Constants;
import game.collectibles.Gem;
import game.components.Component;
import game.stats.node.ResourceNode;
import game.stats.node.ScalarNode;
import game.stats.node.StatsNode;
import game.stores.pools.ability.Ability;
import game.stores.pools.ability.AbilityPool;
import game.stores.pools.unit.Unit;
import logging.ELogger;
import logging.ELoggerFactory;

import java.util.*;

public class Summary extends Component {

    private final ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());

    private String name;
    private final Map<String, StatsNode> stats = new HashMap<>();
    private final Set<String> abilities = new HashSet<>();
    private final Set<String> typing = new HashSet<>();
    
    private Summary() { name = ""; }
    
    public Summary(Unit template) { initialize(template); }
    
    private void initialize(Unit template) {

        name = template.name;

        stats.put(Constants.HEALTH, new ResourceNode(Constants.HEALTH, template.health));
        stats.put(Constants.ENERGY, new ResourceNode(Constants.ENERGY, template.energy));

        stats.put(Constants.PHYSICAL_ATTACK, new ScalarNode(Constants.PHYSICAL_ATTACK, template.physicalAttack));
        stats.put(Constants.PHYSICAL_DEFENSE, new ScalarNode(Constants.PHYSICAL_DEFENSE ,template.physicalDefense));
        stats.put(Constants.MAGICAL_ATTACK, new ScalarNode(Constants.MAGICAL_ATTACK ,template.magicalAttack));
        stats.put(Constants.MAGICAL_DEFENSE, new ScalarNode(Constants.MAGICAL_DEFENSE ,template.magicalDefense));

        stats.put(Constants.SPEED, new ScalarNode(Constants.SPEED, template.speed));
        stats.put(Constants.MOVE, new ScalarNode(Constants.MOVE, template.move));
        stats.put(Constants.CLIMB, new ScalarNode(Constants.CLIMB, template.climb));

        typing.addAll(template.type);
        abilities.addAll(template.abilities);
    }

    public static Summary builder() {
        return new Summary();
    }
    
    public Summary putScalar(String name, int value) { stats.put(name, new ScalarNode(name, value)); return this; }

    public ResourceNode getResourceNode(String key) { return (ResourceNode) stats.get(key); }
    public ScalarNode getScalarNode(String key) { return (ScalarNode) stats.get(key); }
    public StatsNode getNode(String key) { return stats.get(key); }

    public Set<String> getKeySet() { return stats.keySet(); }
    public String getName() { return name; }

    public List<String> getTypes() { return new ArrayList<>(typing); }
    public List<String> getAbilities() { return new ArrayList<>(abilities); }

    @Override
    public String toString() {
        return "Statistics{" +
                "stats=" + stats +
                '}';
    }

    public void addGemBonus(Gem gem) {
        ScalarNode node;
        switch (gem.type) {
            // case HEALTH -> {
            //     node = stats.get(Constants.HEALTH);
            //     node.add(Gem.class, Constants.PERCENT, .5f);
            // }
            // case ENERGY -> {
            //     node = stats.get(Constants.ENERGY);
            //     node.add(Gem.class, Constants.PERCENT, .5f);
            // }
            // case SPEED -> {
            //     node = stats.get(Constants.SPEED);
            //     node.add(Gem.class, Constants.PERCENT, .5f);
            // }
            default -> logger.info("Unsupported gem type {1}", gem.type);
        }
    }
}
