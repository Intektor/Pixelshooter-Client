package de.intektor.pixelshooter.game_state.community_levels.browse_user_levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.game_state.PlayState;
import de.intektor.pixelshooter.game_state.user_level.LevelFolder;
import de.intektor.pixelshooter.game_state.user_level.GuiUserLevelsFolder;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.level.editor.GuiLevelEditor;
import de.intektor.pixelshooter.levels.CommunityPlayInformation;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.world.EditingWorld;
import de.intektor.pixelshooter_common.levels.BasicLevelInformation;
import de.intektor.pixelshooter_common.net.packet.LevelActionPacketToServer;
import de.intektor.pixelshooter_common.net.packet.LevelActionPacketToServer.Action;
import de.intektor.pixelshooter_common.packet.PacketHelper;

import java.io.IOException;

/**
 * @author Intektor
 */
public class GuiViewCommunityLevel extends Gui {

    EditingWorld world;
    BasicLevelInformation info;
    String writtenInfo;

    boolean showWorld;

    final int BUTTON_SHOW_WORLD = 0, BUTTON_PLAY_LEVEL = 1, BUTTON_DOWNLOAD_LEVEL = 2, BUTTON_BACK = 3;

    boolean prevRated;
    int ratedStars;

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_SHOW_WORLD:
                showWorld = !showWorld;
                break;
            case BUTTON_PLAY_LEVEL:
                LevelActionPacketToServer packet = new LevelActionPacketToServer(info.officialID, PixelShooter.playerUUID, Action.PLAY);
                PacketHelper.sendPacket(packet, PixelShooter.mainServerClient.connection);
                PlayState playState = (PlayState) PixelShooter.getGuiByID(PixelShooter.PLAY_STATE);
                playState.setTheWorld(world);
                playState.setPlayInformation(new CommunityPlayInformation(prevRated, ratedStars, info));
                playState.setStart(true);
                PixelShooter.enterGui(PixelShooter.PLAY_STATE);
                break;
            case BUTTON_DOWNLOAD_LEVEL:
                LevelActionPacketToServer packet2 = new LevelActionPacketToServer(info.officialID, PixelShooter.playerUUID, Action.DOWNLOAD);
                PacketHelper.sendPacket(packet2, PixelShooter.mainServerClient.connection);
                EditingWorld.writeToTag(world, world.getName(), world.timeSaved);
                GuiUserLevelsFolder guiUserLevelsFolder = (GuiUserLevelsFolder) PixelShooter.getGuiByID(PixelShooter.USER_LEVELS_FOLDER);
                guiUserLevelsFolder.setLevelFolder(LevelFolder.createFolderByLevelName(world.getName()));
                PixelShooter.enterGui(PixelShooter.USER_LEVELS_FOLDER);
                try {
                    PixelShooter.mainServerClient.connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER);
                break;
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        int x = 100;
        int y = height - 100;
        int squareWidth = width / 2 - 100;

        renderer.begin();

        renderer.setColor(new Color(0xEFEFEFff));

        renderer.set(ShapeRenderer.ShapeType.Filled);

        renderer.rect(0, 0, width, height);

        renderer.set(ShapeRenderer.ShapeType.Line);

        renderer.setColor(new Color(0x777777ff));

        renderer.line(x, y, x, 100);
        renderer.line(x, y - 30, x + squareWidth, y - 30);
        renderer.line(x, y, width - 100, y);
        renderer.line(x + squareWidth, y, x + squareWidth, 100);
        renderer.line(x, 100, width - 100, 100);
        renderer.line(width - 100, height - 100, width - 100, 100);
        renderer.line(width - 100 - squareWidth / 2, height - 100, width - 100 - squareWidth / 2, height - 100 - squareWidth / 2);
        renderer.line(width - 100 - squareWidth / 2, height - 100 - squareWidth / 2, width - 100, height - 100 - squareWidth / 2);

        renderer.end();

        batch.begin();

        BitmapFont bigFont = PixelShooter.unScaledPerfectPixel64;

        bigFont.setColor(new Color(0x3D88C7FF));

        RenderHelper.drawString(this.width / 2, height - 50, world.getName(), bigFont, batch);

        bigFont.setColor(Color.WHITE);

        BitmapFont font22 = PixelShooter.unScaledPerfectPixel22;
        BitmapFont font = font22;

        font.setColor(new Color(0x888888FF));

        RenderHelper.drawString(x + 3, y - 15, "author's comment:", font, batch, false, true);

        font.setColor(new Color(0x898989ff));

        RenderHelper.drawSplitString(x + 3, y - 3 - 30, squareWidth - 16, writtenInfo, font, batch, (int) font.getLineHeight());

        font.setColor(Color.WHITE);

        batch.end();

        if (showWorld) {
            GuiLevelEditor.renderRawWorld(world, width - 100 - squareWidth / 2, height - 100 - squareWidth / 2, squareWidth / 2, squareWidth / 2);
        } else {
            batch.begin();
            PixelShooter.unScaledPerfectPixel128.setColor(Color.RED);
            RenderHelper.drawString(width - 100 - squareWidth / 4, height - 100 - squareWidth / 4, "?", PixelShooter.unScaledPerfectPixel128, batch, true);
            PixelShooter.unScaledPerfectPixel128.setColor(Color.WHITE);
            batch.end();
        }

        batch.begin();

        font22.setColor(new Color(0xFFFF66FF));

        RenderHelper.drawString(width / 2 + 10, y - 15, "Rating:", font22, batch, false, true);

        batch.draw(ImageStorage.empty_stars, width / 2 + 10, y - 80, 150 * 1.5f, 30 * 1.5f);

        float ratio = info.rating / 5;
        batch.draw(new TextureRegion(ImageStorage.full_stars, 0, 0, ratio, 1), width / 2 + 10,  y - 80, 150 * 1.5f * ratio, 30 * 1.5f);

        font22.setColor(new Color(0x898989ff));

        RenderHelper.drawString(width / 2 + 10, y - 100, "Average Rating: " + info.rating, font22, batch, false, true);
        RenderHelper.drawString(width / 2 + 10, y - 100 - font22.getLineHeight(), "Times Rated: " + info.timesRated, font22, batch, false, true);

        RenderHelper.drawString(width / 2 + 10, 100 + squareWidth / 2, "Level Code: ", font22, batch, false);

        RenderHelper.drawSplitString(width / 2 + 10, 100 + squareWidth / 2 - font22.getAscent() - 15, squareWidth - 20, info.officialID, font, batch, (int) font22.getLineHeight());

        font22.setColor(Color.WHITE);

        batch.end();

        super.render(renderer, batch);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public int getID() {
        return PixelShooter.BROWSE_COMMUNITY_LEVELS_VIEW_LEVEL;
    }

    @Override
    public void addGuiComponents() {
        int squareWidth = width / 2 - 100;
        Color TRANSPARENT = new Color(0x00000000);
        componentList.add(new GuiButton(width - 100 - squareWidth / 2, height - 100 - squareWidth / 2, squareWidth / 2, squareWidth / 2, "", BUTTON_SHOW_WORLD, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, true));
        componentList.add(new GuiButton(width - 100 - squareWidth / 2, 100, squareWidth / 2, 100, "Play", BUTTON_PLAY_LEVEL, new Color(0x3D88C7ff), new Color(0x3D88C7ff), new Color(0x3D88C7ff), new Color(0x3D88C7ff), new Color(0x3D88C7ff), new Color(0x3D88C7ff), true));
        componentList.add(new GuiButton(width - 100 - squareWidth, 100, squareWidth / 2, 100, "Download", BUTTON_DOWNLOAD_LEVEL, new Color(0xB300E5), new Color(0xB300E5), new Color(0xB300E5), new Color(0xB300E5), new Color(0xB300E5), new Color(0xB300E5), true));
        componentList.add(new GuiButton(100, 100, squareWidth / 2, 100, "Back", BUTTON_BACK, new Color(0xFD8181FF), new Color(0xFD8181FF), new Color(0xFD8181FF), new Color(0xFD8181FF), new Color(0xFD8181FF), new Color(0xFD8181FF), true));
    }

    public void setInfo(EditingWorld world, BasicLevelInformation info, String written_info, boolean prevRated, int ratedStars) {
        this.world = world;
        this.info = info;
        this.writtenInfo = written_info;
        showWorld = false;
        this.prevRated = prevRated;
        this.ratedStars = ratedStars;
    }
}
