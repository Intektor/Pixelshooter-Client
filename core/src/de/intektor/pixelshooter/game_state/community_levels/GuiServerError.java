package de.intektor.pixelshooter.game_state.community_levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.TickTimerHandler;

/**
 * @author Intektor
 */
public class GuiServerError extends Gui {

    final String TICK_TIMER_WAIT_TILL_EXIT_GUI = "TICK_TIMER_WAIT_TILL_EXIT_GUI_SERVER_ERROR_GUI";

    String errorMessage = "";

    public GuiServerError() {
        TickTimerHandler.registerTickTimer(120, TICK_TIMER_WAIT_TILL_EXIT_GUI);
    }

    @Override
    public void enterGui() {
        super.enterGui();
        TickTimerHandler.resetTickTimer(TICK_TIMER_WAIT_TILL_EXIT_GUI);
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        batch.begin();
        RenderHelper.drawString(width / 2, height / 2, errorMessage, PixelShooter.unScaledPerfectPixel32, batch);
        batch.end();
    }

    @Override
    public void update() {
        super.update();
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_WAIT_TILL_EXIT_GUI)) {
            PixelShooter.enterGui(PixelShooter.MAIN_MENU);
        }
    }

    @Override
    public void onButtonTouched(int id) {

    }

    @Override
    public int getID() {
        return PixelShooter.SERVER_CONNECTION_SHUTDOWN_MESSAGE;
    }

    @Override
    public void addGuiComponents() {

    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
