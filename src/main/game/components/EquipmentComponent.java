package main.game.components;

import main.logging.EmeritusLogger;

public class EquipmentComponent extends Component {
    private final static String HEAD_SLOT = "head";
    public void equipToHead(String equipmentID) { put(HEAD_SLOT, equipmentID); }
    public String getEquippedToHead() { return getString(HEAD_SLOT); }

    private final static String NECK_SLOT = "neck";
    public void equipToNeck(String equipmentID) { put(NECK_SLOT, equipmentID); }
    public String getEquippedToNeck() { return getString(NECK_SLOT); }

    private final static String FOOT_SLOT = "foot";
    public void equipToFoot(String equipmentID) { put(FOOT_SLOT, equipmentID); }
    public String getEquippedToFoot() { return getString(FOOT_SLOT); }

    private final static String TORSO_SLOT = "torso";
    public void equipToTorso(String equipmentID) { put(TORSO_SLOT, equipmentID); }
    public String getEquippedToTorso() { return getString(TORSO_SLOT); }

    private final static String BELT_SLOT = "belt";
    public void equipToBelt(String equipmentID) { put(BELT_SLOT, equipmentID); }
    public String getEquippedToBelt() { return getString(BELT_SLOT); }

    private final static String LEFT_HAND_SLOT = "left_hand";
    public void equipToLeftHand(String equipmentID) { put(LEFT_HAND_SLOT, equipmentID); }
    public String getEquippedToLeftHand() { return getString(LEFT_HAND_SLOT); }

    private final static String RIGHT_HAND_SLOT = "right_hand";
    public void equipToRightHand(String equipmentID) { put(RIGHT_HAND_SLOT, equipmentID); }
    public String getEquippedToRightHand() { return getString(RIGHT_HAND_SLOT); }

    private final static String POCKET_SLOT = "pocket";
    public void equipToPocket(String equipmentID) { put(POCKET_SLOT, equipmentID); }
    public String getEquippedToPocket() { return getString(POCKET_SLOT); }

    private final static String BACK_SLOT = "back";
    public void equipToBack(String equipmentID) { put(BACK_SLOT, equipmentID); }
    public String getEquippedToBack() { return getString(BACK_SLOT); }


    private final static EmeritusLogger logger = EmeritusLogger.create(EquipmentComponent.class);

    public boolean equip(String equipSlot, String equipmentID) {
        boolean isValidSlot = isValidSlot(equipSlot);
        if (!isValidSlot) {
            return false;
        }
        put(equipSlot, equipmentID);
        return true;
    }

    private static boolean isValidSlot(String slot) {
        return slot.contains(HEAD_SLOT) || slot.contains(NECK_SLOT) || slot.contains(FOOT_SLOT) ||
                slot.contains(TORSO_SLOT) || slot.contains(BELT_SLOT) || slot.contains(LEFT_HAND_SLOT) ||
                slot.contains(RIGHT_HAND_SLOT) || slot.contains(POCKET_SLOT) || slot.contains(BACK_SLOT);
    }
}
