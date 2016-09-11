package de.intektor.pixelshooter.levels;

import de.intektor.pixelshooter.enums.PlayStateStatus;
import de.intektor.pixelshooter_common.levels.BasicLevelInformation;

/**
 * @author Intektor
 */
public class CommunityPlayInformation implements PlayInformation {

    public final boolean alreadyRated;
    public final int ratedStars;
    public final BasicLevelInformation info;

    public CommunityPlayInformation(boolean alreadyRated, int ratedStars, BasicLevelInformation info) {
        this.alreadyRated = alreadyRated;
        this.info = info;
        this.ratedStars = ratedStars;
    }

    @Override
    public PlayStateStatus getStatus() {
        return PlayStateStatus.COMMUNITY_LEVEL;
    }
}
