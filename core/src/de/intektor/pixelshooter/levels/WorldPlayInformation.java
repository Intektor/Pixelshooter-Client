package de.intektor.pixelshooter.levels;

import de.intektor.pixelshooter.enums.PlayStateStatus;

/**
 * @author Intektor
 */
public class WorldPlayInformation implements PlayInformation {

    public final int worldID;
    public final int levelID;

    public WorldPlayInformation(int worldID, int level_id) {
        this.worldID = worldID;
        this.levelID = level_id;
    }

    @Override
    public PlayStateStatus getStatus() {
        return PlayStateStatus.WORLD_LEVEL;
    }
}
