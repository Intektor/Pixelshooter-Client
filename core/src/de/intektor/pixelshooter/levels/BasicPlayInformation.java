package de.intektor.pixelshooter.levels;

import de.intektor.pixelshooter.enums.PlayStateStatus;

/**
 * @author Intektor
 */
public class BasicPlayInformation implements PlayInformation {

    public final PlayStateStatus status;

    public BasicPlayInformation(PlayStateStatus status) {
        this.status = status;
    }

    @Override
    public PlayStateStatus getStatus() {
        return status;
    }
}
