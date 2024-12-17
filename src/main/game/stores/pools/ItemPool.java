package main.game.stores.pools;

import main.game.components.InventoryComponent;
import main.game.components.StatisticsComponent;
import main.game.entity.Entity;
import main.game.stores.factories.EntityFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.RandomUtils;

import java.util.HashMap;
import java.util.SplittableRandom;

public class ItemPool {

    private static final SplittableRandom random = new SplittableRandom();

    private static ItemPool mInstance = null;
    public static ItemPool getInstance() { if (mInstance == null) { mInstance = new ItemPool(); } return mInstance; }

    private ItemPool() {
        ELogger logger = ELoggerFactory.getInstance().getELogger(getClass());
        logger.info("Started initializing {}", getClass().getSimpleName());

        try {
//            FileReader reader = new FileReader(Constants.UNITS_DATA_FILE_JSON);
//            JSONObject objects = (JSONObject) Jsoner.deserialize(reader);
//            for (Object object : objects.values()) {
//                JSONObject dao = (JSONObject) object;
//                UnitTemplate unitTemplate = new UnitTemplate(dao);
//                mUnitTemplateMap.put(unitTemplate.name.toLowerCase(Locale.ROOT), unitTemplate);
//            }
        } catch (Exception ex) {
            logger.error("Error parsing prototype: " + ex.getMessage());
            ex.printStackTrace();
        }

        logger.info("Finished initializing {}", getClass().getSimpleName());
    }
    public Entity create(int level) {
        String[][] types = new String[][]{
                new String[]{ "Weapon", "Sword", "Staff", "Wand", "Knife", "Dagger", "Mace" },
                new String[]{ "Head", "Helmet" },
                new String[]{ "Torso", "Chain-mail", "Plate-mail", "Coat", "Jacket" },
                new String[]{ "Arms", "Gloves" },
                new String[]{ "Legs", "Pants" },

                new String[]{ "Jewelry", "Ring" },
                new String[]{ "Item", "Test Item" }
        };

        int index = random.nextInt(types.length);
        String[] categoryAndNames = types[index];
        String name = categoryAndNames[random.nextInt(1, categoryAndNames.length)];
        String category = categoryAndNames[0];
        String randomName = RandomUtils.createRandomName(3, 6);

        HashMap<String, Integer> map = new HashMap<>();

//        if (random.nextBoolean()) { map.put(StatisticsComponent.HEALTH, random.nextInt(1, level)); }
//        if (random.nextBoolean()) { map.put(StatisticsComponent.MANA, random.nextInt(1, level)); }
//        if (random.nextBoolean()) { map.put(StatisticsComponent.PHYSICAL_ATTACK, random.nextInt(1, level)); }
//        if (random.nextBoolean()) { map.put(StatisticsComponent.PHYSICAL_DEFENSE, random.nextInt(1, level)); }
//        if (random.nextBoolean()) { map.put(StatisticsComponent.MAGICAL_ATTACK, random.nextInt(1, level)); }
//        if (random.nextBoolean()) { map.put(StatisticsComponent.MAGICAL_DEFENSE , random.nextInt(1, level)); }
//        if (random.nextBoolean()) { map.put(StatisticsComponent.SPEED , random.nextInt(1, level)); }

        Entity entity = new Entity();
//        EntityFactory.create(name + " of " + randomName);
//        entity.add(new StatisticsComponent(map));

        return entity;
    }

    public void equip(Entity entity, Entity item) {

        StatisticsComponent statisticsComponent = entity.get(StatisticsComponent.class);
        InventoryComponent inventoryComponent = entity.get(InventoryComponent.class);
    }
}
