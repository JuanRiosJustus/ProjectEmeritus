package game.systems;

import constants.ColorPalette;
import engine.EngineController;
import game.GameModel;
import game.components.Movement;
import game.components.SpriteAnimation;
import game.components.statistics.Statistics;
import game.entity.Entity;
import game.systems.FloatingTextSystem;
import game.systems.combat.CombatEvent;
import game.systems.combat.DamageReport;

public class StatusEffectSystem extends GameSystem {

    @Override
    public void update(GameModel model, Entity unit) {
//        Statistics statistics = unit.get(Statistics.class);
//
//        for (String effect : statistics.getStatusEffects()) {
//
//            switch (effect) {
//                case "burn" -> handleBurn(unit);
//            }
//        }
    }

    private static void handleBurn(Entity unit) {
//        Statistics statistics = unit.get(Statistics.class);
//        SpriteAnimation spriteAnimation = unit.get(SpriteAnimation.class);
//        statistics.toHealth(-10);
//        FloatingTextSystem.floater("Burning", spriteAnimation.position, ColorPalette.RED);
    }

//    private SplittableRandom m_random = new SplittableRandom();
//    private Set<String> m_statuses = new HashSet<>();
//    private Deque<String> m_toRemove = new LinkedList<>();
//
//    public void add(String status) { m_statuses.add(status.toLowerCase(Locale.ROOT)); }
//
//    public void handle(GameModel model, Creature me) {
//        m_toRemove.clear();
//        for (String status : m_statuses) {
//            if (m_random.nextDouble() <= .25) {
//                // remove the status
//                m_toRemove.add(status);
//            } else {
//                // handle the status
//                handleEffect(model, me, status);
//            }
//        }
//
//        while (m_toRemove.size() > 0) { m_statuses.remove(m_toRemove.poll()); }
//    }
//
//    private void handleEffect(GameModel model, Creature me, String effect) {
//        switch (effect) {
//            case "burned" -> handleBurned(model, me);
//            case "poisoned" -> handlePoisoned(model, me);
//            case "confused" -> handleConfused(model, me);
//            default -> EmeritusLogger.get().log(effect + " is not an effect");
//        }
//    }
//
//    private void handlePoisoned(GameModel model, Creature me) {
////        int currentHealth = me.getCurrentHealth();
////        double eightPercentOfCurrent = currentHealth * .08;
////        double newHealth = currentHealth - eightPercentOfCurrent;
////        me.setCurrentHealth((int) newHealth);
////        model.getMainPanel().getLog().log(me.getName() + " is badly poisoned.");
//    }
//
//    private void handleBurned(GameModel model, Creature me) {
////        me.setCurrentHealth(me.getCurrentHealth() - 10);
////        model.getMainPanel().getLog().log(me.getName() + " was burned.");
//    }
//
//    private void handleConfused(GameModel model, Creature me) {
////        int currentHealth = me.getCurrentHealth();
////        int damageToDeal = (int) (me.getCombat(Combat.NormalAttack).getTotal() * .25);
////        int newHealth = currentHealth - damageToDeal;
////        me.setCurrentHealth(newHealth);
////        model.getMainPanel().getLog().log(me.getName() + " was hurt from confusion.");
////        model.getFloatingContext().add(
////                "confused-" + damageToDeal, me.getLocalPositionX(),
////                me.getLocalPositionY(),
////                Color.RED
////        );
//    }
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (String status : m_statuses) {
//            sb.append(status).append(' ');
//        }
//        sb.trimToSize();
//        return sb.toString();
//    }

//    private static void handleCounterStatusEffect(EngineController engine, Entity attacker, CombatEvent event,
//                                                  Entity defender, Statistics defendingStats,
//                                                  Statistics attackingStats, DamageReport health, DamageReport energy) {
////                                                  Statistics attackingStats, int healthDamage, int energyDamage) {
//        // handle all the damage dealt back to the attacker at quarter power
//        int healthDamageToDeal = 0, healthDamageToHeal = 0, energyDamageToDeal = 0, energyDamageToHeal = 0;
//        int healthDamage = health.getTotalDamage(), energyDamage = energy.getTotalDamage();
//        if (energyDamage != 0) {
//            energyDamageToDeal = (int) (energyDamage * .25);
//            logger.log("{0} countered {1} for {2} health damage", defender, attacker, energyDamageToDeal);
//            attackingStats.toEnergy(energyDamageToDeal);
//        }
//        if (healthDamage != 0) {
//            healthDamageToDeal = (int) (healthDamage * .25);
//            logger.log("{0} countered {1} for {2} health damage", defender, attacker, healthDamageToDeal);
//            attackingStats.toHealth(healthDamageToDeal);
//        }
//        // This should be hit most of the time
//        if (healthDamageToDeal != 0 || energyDamageToDeal != 0) {
//            Movement movement = defender.get(Movement.class);
//            movement.gyrate(engine, defender);
//            FloatingTextSystem.floater("Countered!",
//                    defender.get(SpriteAnimation.class).position, ColorPalette.getColorBasedOnAbility(event.ability));
//
//            // Only show one type of damage dealt
//            if (healthDamageToDeal != 0) {
//                FloatingTextSystem.floater(String.valueOf(healthDamageToDeal),
//                        attacker.get(SpriteAnimation.class).position,
//                        ColorPalette.getColorBasedOnAbility(event.ability));
//            } else {
//                FloatingTextSystem.floater(String.valueOf(energyDamageToDeal),
//                        attacker.get(SpriteAnimation.class).position,
//                        ColorPalette.getColorBasedOnAbility(event.ability));
//            }
//        }
//    }
}
