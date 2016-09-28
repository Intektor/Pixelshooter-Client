package de.intektor.pixelshooter.game_state.community_levels;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.net.client.MainServerClient;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.StringUtils;
import de.intektor.pixelshooter.util.TickTimerHandler;

/**
 * @author Intektor
 */
public class GuiConnectToMainServer extends Gui {

    public int nextState, prevState;

    public volatile long timeAtTryConnection;

    final String TICK_TIMER_TICK_CONNECTION = "CONNECT_TO_MAIN_SERVER_GUI_TICK_CONNECTION_GUI";
    final String TICK_TIMER_JOIN_FOLLOWING_GUI = "JOIN_FOLLOWING_GUI_TICK_MAIN_SERVER_CONNECTION_GUI";

    public GuiConnectToMainServer() {
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_TICK_CONNECTION);
        TickTimerHandler.registerTickTimer(60, TICK_TIMER_JOIN_FOLLOWING_GUI);
    }

    volatile boolean connectionCrashed;

    volatile Exception thrownException;

    @Override
    public void enterGui() {
        super.enterGui();
        reset();
        timeAtTryConnection = System.currentTimeMillis();
        final MainServerClient client = PixelShooter.mainServerClient;
        new Thread() {
            @Override
            public void run() {
                try {
                    client.run();
                } catch (java.net.ConnectException e) {
                    connectionCrashed = true;
                    e.printStackTrace();
                    thrownException = e;
                } catch (Exception e) {
                    e.printStackTrace();
                    thrownException = e;
                }
            }
        }.start();

    }

    String connectToMainServerString = "";
    int connectMainServerTick;
    int followingGui = -1;

    @Override
    public void update() {
        super.update();
        if (System.currentTimeMillis() - timeAtTryConnection >= 5000) {
            connectionCrashed = true;
            thrownException = new Exception("Timeout!");
        }
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_TICK_CONNECTION)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_TICK_CONNECTION);
            connectToMainServerString = "Connecting to main server" + StringUtils.repeat(".", connectMainServerTick);
            if (connectMainServerTick == 3) {
                connectMainServerTick = 0;
            } else {
                connectMainServerTick++;
            }
        }
        if (followingGui == -1) {
            if (PixelShooter.mainServerClient.connection != null) {
                if (PixelShooter.mainServerClient.connection.isConnected()) {
                    followingGui = nextState;
                    TickTimerHandler.resetTickTimer(TICK_TIMER_JOIN_FOLLOWING_GUI);
                }
            } else if (connectionCrashed) {
                followingGui = prevState;
                TickTimerHandler.resetTickTimer(TICK_TIMER_JOIN_FOLLOWING_GUI);
            }
        }
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_JOIN_FOLLOWING_GUI) && followingGui != -1) {
            PixelShooter.enterGui(followingGui);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        String shownString = null;
        BitmapFont font = PixelShooter.unScaledPerfectPixel32;
        if (PixelShooter.mainServerClient.connection != null) {
            if (PixelShooter.mainServerClient.connection.isConnected()) {
                shownString = "Successfully connected!";
            }
        } else if (connectionCrashed) {
            shownString = thrownException != null ? thrownException.getLocalizedMessage() : "Error!";
        } else {
            shownString = connectToMainServerString;
        }
        RenderHelper.drawString(width / 2, height / 2, shownString, font, b, true);
        b.end();
    }

    public void reset() {
        connectionCrashed = false;
        PixelShooter.mainServerClient = new MainServerClient();
        connectMainServerTick = 0;
        followingGui = -1;
        thrownException = null;
    }

    @Override
    public void onButtonTouched(int id) {

    }

    @Override
    public int getID() {
        return PixelShooter.CONNECT_TO_MAIN_SERVER;
    }

    @Override
    public void addGuiComponents() {

    }

    public void setInfo(int nextState, int prevState) {
        this.nextState = nextState;
        this.prevState = prevState;
    }
}
