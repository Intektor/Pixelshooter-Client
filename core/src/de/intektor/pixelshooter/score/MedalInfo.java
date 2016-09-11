package de.intektor.pixelshooter.score;

import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;

/**
 * @author Intektor
 */
public class MedalInfo {

    public final int minBronze, minSilver, minGold;

    public MedalInfo(int minBronze, int minSilver, int minGold) {
        this.minBronze = minBronze;
        this.minSilver = minSilver;
        this.minGold = minGold;
    }

    public void writeToTag(PSTagCompound tag) {
        tag.setInteger("min_bronze", minBronze);
        tag.setInteger("min_silver", minSilver);
        tag.setInteger("min_gold", minGold);
    }

    public static MedalInfo readFromTag(PSTagCompound tag) {
        return new MedalInfo(tag.getInteger("min_bronze"), tag.getInteger("min_silver"), tag.getInteger("min_gold"));
    }
}
