package de.intektor.pixelshooter.game_state.community_levels.browse_user_levels;

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
public class WaitForServerToSendLevelInfo extends Gui {

    final String TICK_TIMER_UPDATE_STRING = "WAIT_FOR_SERVER_TO_SEND_LEVEL_INFO_TICK_TIMER_UPDATE_STRING";
    final String TICK_TIMER_SHOW_ERROR_MESSAGE = "WAIT_FOR_SERVER_TO_SEND_LEVEL_INFO_TICK_TIMER_SHOW_ERROR_MESSAGE";

    int stringUpdateTicker;

    boolean error;

    public WaitForServerToSendLevelInfo() {
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_UPDATE_STRING);
        TickTimerHandler.registerTickTimer(60, TICK_TIMER_SHOW_ERROR_MESSAGE);
    }

    @Override
    public void init() {
        super.init();
        reset();
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        batch.begin();

        String shownString = "";
        if (error) {
            shownString = "Couldn't get level data";
        } else {
            shownString = "Receiving level data" + StringUtils.repeat(".", stringUpdateTicker);
        }

        RenderHelper.drawString(width / 2, height / 2, shownString, PixelShooter.unScaledPerfectPixel32, batch);
        batch.end();
    }

    @Override
    public void update() {
        super.update();
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_UPDATE_STRING)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_UPDATE_STRING);
            stringUpdateTicker++;
            if (stringUpdateTicker == 4) {
                stringUpdateTicker = 0;
            }
        }
        if (error && TickTimerHandler.hasTickTimerFinished(TICK_TIMER_SHOW_ERROR_MESSAGE)) {
            PixelShooter.enterGui(PixelShooter.BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER);
        }
    }

    @Override
    public void onButtonTouched(int id) {

    }

    @Override
    public int getID() {
        return PixelShooter.WAIT_FOR_SERVER_TO_SEND_LEVEL_DATA;
    }

    @Override
    public void addGuiComponents() {

    }

    public void reset() {
        error = false;
    }

    public void setError() {
        TickTimerHandler.resetTickTimer(TICK_TIMER_SHOW_ERROR_MESSAGE);
        error = true;
    }
}
