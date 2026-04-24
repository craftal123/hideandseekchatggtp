package com.example.hardesthide.powerups;

public enum PowerupType {
    SMOKE_BURST("Smoke Burst", 1, "Creates smoke particles around the hider."),
    TEMP_INVISIBILITY("Temporary Invisibility", 3, "Makes the hider invisible for a short time."),
    FAKE_FOOTSTEPS("Fake Footsteps", 1, "Plays fake step sounds elsewhere."),
    DECOY_MOB("Spawn Decoy Mob", 2, "Spawns a fake distraction mob."),
    FREEZE_HUNTERS("Freeze Hunters", 4, "Briefly freezes hunters."),
    SHUFFLE_MARKERS("Shuffle Minimap Markers", 3, "Temporarily scrambles hunter minimap markers."),
    ADD_TIME("Add Extra Time", 2, "Adds survival time for the hider."),
    SHORT_TELEPORT("Short Teleport", 4, "Teleports the hider a short distance."),
    BLACKOUT_SECTION("Blackout Random Map Section", 3, "Adds a confusing dark map section.");

    public final String title;
    public final int cost;
    public final String tooltip;

    PowerupType(String title, int cost, String tooltip) {
        this.title = title;
        this.cost = cost;
        this.tooltip = tooltip;
    }

    public static PowerupType byId(String id) {
        for (PowerupType type : values()) {
            if (type.name().equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}
