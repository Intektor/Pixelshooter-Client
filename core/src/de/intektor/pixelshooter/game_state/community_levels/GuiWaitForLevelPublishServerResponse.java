package de.intektor.pixelshooter.game_state.community_levels;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.StringUtils;
import de.intektor.pixelshooter.util.TickTimerHandler;

/**
 * @author Intektor
 */
public class GuiWaitForLevelPublishServerResponse extends Gui {

    final String TICK_TIMER_TICK_CONNECTION = "PUBLISH_LEVEL_GUI_TICK_CONNECTION_GUI";
    final String TICK_TIMER_JOIN_FOLLOWING_GUI = "JOIN_FOLLOWING_GUI_TICK_WAIT_FOR_RESPONSE_GUI";

    private Response response;

    public GuiWaitForLevelPublishServerResponse() {
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_TICK_CONNECTION);
        TickTimerHandler.registerTickTimer(120, TICK_TIMER_JOIN_FOLLOWING_GUI);
    }

    @Override
    public void init() {
        super.init();
        reset();
    }

    @Override
    public void onButtonTouched(int id) {

    }

    @Override
    public int getID() {
        return PixelShooter.WAIT_FOR_PUBLISH_LEVEL_RESPONSE;
    }

    String sendPacketToMainServerString = "";
    int connectMainServerTick;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        BitmapFont font = PixelShooter.unScaledPerfectPixel32;
        String text = null;
        if (response == null) {
            text = sendPacketToMainServerString;
        } else switch (response) {
            case BAD_LOGIN:
                text = "Error: Bad access token. Try a relog!";
                break;
            case LEVEL_PUBLISHED:
                text = "Successfully uploaded level to the main server!";
                break;
            case INTERNAL_SERVER_ERROR:
                text = "Internal server error";
                break;
        }
        RenderHelper.drawString(width / 2, height / 2, text, font, b, true);
        b.end();
    }

    @Override
    public void update() {
        super.update();
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_TICK_CONNECTION)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_TICK_CONNECTION);
            sendPacketToMainServerString = "Sending Level and waiting for response" + StringUtils.repeat(".", connectMainServerTick);
            if (connectMainServerTick == 3) {
                connectMainServerTick = 0;
            } else {
                connectMainServerTick++;
            }
        }
        if (response != null && TickTimerHandler.hasTickTimerFinished(TICK_TIMER_JOIN_FOLLOWING_GUI)) {
            switch (response) {
                case BAD_LOGIN:
                    PixelShooter.enterGui(PixelShooter.MAIN_MENU);
                    break;
                case LEVEL_PUBLISHED:
                    PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                    break;
                case INTERNAL_SERVER_ERROR:
                    PixelShooter.enterGui(PixelShooter.PUBLISH_LEVEL_TO_MAIN_SERVER);
                    break;
            }
        }
    }

    @Override
    public void addGuiComponents() {

    }

    public void reset() {
        response = null;
    }

    public void setResponse(Response response) {
        this.response = response;
        TickTimerHandler.resetTickTimer(TICK_TIMER_JOIN_FOLLOWING_GUI);
    }

    public enum Response {
        BAD_LOGIN,
        LEVEL_PUBLISHED,
        INTERNAL_SERVER_ERROR,
    }
}
