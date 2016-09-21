package de.intektor.pixelshooter.game_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.render.RenderHelper;

/**
 * @author Intektor
 */
public class GuiMainMenu extends Gui {

    final int START_GAME = 0, OPTIONS = 1, EXIT_GAME = 2, LOGIN_WITH_GOOGLE = 3;

    @Override
    public void init() {
        super.init();
        getButtonByID(LOGIN_WITH_GOOGLE).setDescription(PixelShooter.googleAccount == null ? "Login with Google!" : "Logout!");
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case START_GAME:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                break;
            case OPTIONS:
                PixelShooter.enterGui(PixelShooter.OPTIONS);
                break;
            case EXIT_GAME:
                Gdx.app.exit();
                break;
            case LOGIN_WITH_GOOGLE:
                if (PixelShooter.googleAccount == null) {
                    PixelShooter.enterGui(PixelShooter.CONNECT_TO_GOOGLE);
                } else {
                    Gdx.files.local("login").delete();
                    PixelShooter.googleAccount = null;
                    getButtonByID(LOGIN_WITH_GOOGLE).setDescription("Login with Google!");
                }
                break;
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        batch.begin();
        RenderHelper.drawString(width / 2, height / 10 * 9, "Pixelshooter", PixelShooter.unScaledPerfectPixel128, PixelShooter.spriteBatch);
        if (PixelShooter.googleAccount != null) {
            RenderHelper.drawString(0, 22, "Logged in as: " + PixelShooter.googleAccount.getEmail(), PixelShooter.unScaledPerfectPixel22, PixelShooter.spriteBatch, false);
        } else {
            RenderHelper.drawString(0, 22, "Not logged in!", PixelShooter.unScaledPerfectPixel22, PixelShooter.spriteBatch, false);
        }
        batch.end();
        super.render(renderer, batch);
    }

    @Override
    public int getID() {
        return PixelShooter.MAIN_MENU;
    }

    @Override
    public void addGuiComponents() {
        int x = width / 4;
        int y = height / 2 / 3;
        componentList.add(new GuiButton(x, y + height / 2 / 4 * 3, width / 2, height / 2 / 4, "Start Game", START_GAME, true));
        componentList.add(new GuiButton(x, y + height / 2 / 4 * 2, width / 2, height / 2 / 4, "Options", OPTIONS, true));
        componentList.add(new GuiButton(x, y + height / 2 / 4, width / 2, height / 2 / 4, "Login with Google", LOGIN_WITH_GOOGLE, true));
        componentList.add(new GuiButton(x, y, width / 2, height / 2 / 4, "Exit Game", EXIT_GAME, true));
    }

}
