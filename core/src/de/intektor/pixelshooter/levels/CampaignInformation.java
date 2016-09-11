package de.intektor.pixelshooter.levels;

import de.intektor.pixelshooter.enums.Medals;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Intektor
 */
public class CampaignInformation {

    public List<WorldInformation> worlds = new ArrayList<WorldInformation>();

    public CampaignInformation() {
        worlds.add(new WorldInformation(40));
    }

    public void levelFinished(int worldID, int levelID) {
        worlds.get(worldID).levelState = levelID;
    }

    public boolean levelPlayable(int worldID, int levelID) {
        return worlds.get(worldID).levelState >= levelID;
    }

    public void writeToTag(PSTagCompound tag) throws IOException {
        tag.setInteger("amt_worlds", worlds.size());
        for (int i = 0; i < worlds.size(); i++) {
            WorldInformation world = worlds.get(i);
            PSTagCompound worldTag = new PSTagCompound();
            world.writeToTag(worldTag);
            tag.setTag("world" + i, worldTag);
        }
    }

    public static CampaignInformation readFromTag(PSTagCompound tag) throws IOException {
        CampaignInformation info = new CampaignInformation();
        int worlds = tag.getInteger("amt_worlds");
        for (int i = 0; i < worlds; i++) {
            info.worlds.add(WorldInformation.readFromTag(tag.getTag("world" + i)));
        }
        return info;
    }

    public static class WorldInformation {

        public List<LevelInformation> levels = new ArrayList<LevelInformation>();

        public int levelState;

        public WorldInformation(int levels) {
            this.levels = new ArrayList<LevelInformation>(levels);
            for (int i = 0; i < levels; i++) {
                this.levels.add(new LevelInformation(i));
            }
        }

        public WorldInformation() {
        }

        public WorldInformation(List<LevelInformation> levels) {
            this.levels = levels;
        }

        public LevelInformation getLevel(int number) {
            return levels.get(number);
        }

        public void writeToTag(PSTagCompound tag) throws IOException {
            tag.setInteger("level_state", levelState);
            tag.setInteger("amt_of_lvls", levels.size());
            for (int i = 0; i < levels.size(); i++) {
                LevelInformation level = levels.get(i);
                PSTagCompound levelTag = new PSTagCompound();
                level.writeToTag(levelTag);
                tag.setTag("level" + i, levelTag);
            }
        }

        public static WorldInformation readFromTag(PSTagCompound tag) throws IOException {
            WorldInformation info = new WorldInformation();
            info.levelState = tag.getInteger("level_state");
            int levels = tag.getInteger("amt_of_lvls");
            for (int i = 0; i < levels; i++) {
                PSTagCompound levelTag = tag.getTag("level" + i);
                info.levels.add(LevelInformation.readFromTag(levelTag));
            }
            return info;
        }
    }

    public static class LevelInformation {

        public int levelID;
        public Medals medal = Medals.NONE;

        public LevelInformation(int levelID) {
            this.levelID = levelID;
        }

        public void writeToTag(PSTagCompound tag) throws IOException {
            tag.setInteger("level_id", levelID);
            tag.setInteger("medal", medal.ordinal());
        }

        public static LevelInformation readFromTag(PSTagCompound tag) throws IOException {
            LevelInformation info = new LevelInformation(tag.getInteger("level_id"));
            info.medal = Medals.values()[tag.getInteger("medal")];
            return info;
        }
    }
}
