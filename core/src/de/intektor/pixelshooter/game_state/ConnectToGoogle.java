package de.intektor.pixelshooter.game_state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.net.HttpRequestHeader;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.auth.GoogleAccount;
import de.intektor.pixelshooter.util.StringUtils;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.TickTimerHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Intektor
 */
public class ConnectToGoogle extends Gui {

    final int BUTTON_BACK = 0, BUTTON_LOGIN = 1;

    boolean connectingToGoogle;

    boolean failedToConnect = false;
    Throwable exception;
    boolean connectedToGoogle;
    boolean successfullyLoggedIn;

    JSONObject receivedJson;

    final String TICK_TIMER_TICK_CONNECTION = "CONNECT_TO_GOOGLE_TICK_CONNECTION_GUI";

    long timeReceivedResponseFromGoogle;

    public ConnectToGoogle() {
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_TICK_CONNECTION);
    }

    @Override
    public void init() {
        super.init();
        reset();
        Net.HttpRequest r = new Net.HttpRequest(Net.HttpMethods.POST);
        r.setHeader(HttpRequestHeader.ContentType, "application/x-www-form-urlencoded");
        r.setUrl("https://accounts.google.com/o/oauth2/device/code");
        r.setContent("client_id=775862664818-4ejmh7ap6nkvjethc6k0l7ond182bj01.apps.googleusercontent.com&scope=email%20profile");

        connectingToGoogle = true;

        Gdx.net.sendHttpRequest(r, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                try {
                    receivedJson = new JSONObject(httpResponse.getResultAsString().trim());

                    connectingToGoogle = false;
                    connectedToGoogle = true;

                    getButtonByID(BUTTON_LOGIN).setShown(true);

                    timeReceivedResponseFromGoogle = System.currentTimeMillis();
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable t) {
                connectingToGoogle = false;
                failedToConnect = true;
                exception = t;
            }

            @Override
            public void cancelled() {
                connectingToGoogle = false;
                failedToConnect = true;
            }
        });
    }

    String connectToGoogleString = "Connecting to Google";

    int connectGoogleTick;

    long lastCheckTime;

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        super.render(renderer, batch);
        SpriteBatch b = PixelShooter.spriteBatch;
        b.begin();
        BitmapFont font = PixelShooter.unScaledPerfectPixel64;
        if (connectingToGoogle) {
            RenderHelper.drawString(width / 2, height / 2, connectToGoogleString, font, b, true);
        }
        if (failedToConnect) {
            RenderHelper.drawString(width / 2, height / 2, "Failed to connect to Google:", font, b, true);
            RenderHelper.drawString(width / 2, height / 2 - font.getLineHeight(), exception.getLocalizedMessage(), PixelShooter.unScaledPerfectPixel22, b, true);
        }
        if (connectedToGoogle) {
            if (!successfullyLoggedIn) {
                RenderHelper.drawString(width / 2, height - 50, "Successfully connected to Google!", PixelShooter.unScaledPerfectPixel64, b, true);
                String verification_url = null;
                String user_code = null;
                try {
                    verification_url = receivedJson.getString("verification_url");
                    user_code = receivedJson.getString("user_code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                BitmapFont infoFont = PixelShooter.unScaledPerfectPixel32;
                RenderHelper.drawString(width / 2, height - 120, "Verification URL:", infoFont, b, true);
                RenderHelper.drawString(width / 2, height - 120 - infoFont.getLineHeight(), verification_url, infoFont, b, true);

                RenderHelper.drawString(width / 2, height - 120 - infoFont.getLineHeight() * 3, "User Code:", infoFont, b, true);
                RenderHelper.drawString(width / 2, height - 120 - infoFont.getLineHeight() * 4, user_code, infoFont, b, true);

                RenderHelper.drawString(width / 2, height - 120 - infoFont.getLineHeight() * 6, "You have to open the browser and enter the code in order to login!", infoFont, b, true);
            } else {
                RenderHelper.drawString(width / 2, height / 2 + font.getLineHeight(), "Successfully logged in as:", font, b, true);
                try {
                    RenderHelper.drawString(width / 2, height / 2, PixelShooter.googleAccount.getEmail(), font, b, true);
                } catch (Exception ignored) {
                }

            }
        }
        b.end();
    }

    @Override
    public void update() {
        super.update();
        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_TICK_CONNECTION)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_TICK_CONNECTION);
            connectToGoogleString = "Connecting to Google" + StringUtils.repeat(".", connectGoogleTick);
            if (connectGoogleTick == 3) {
                connectGoogleTick = 0;
            } else {
                connectGoogleTick++;
            }
        }
        if (connectedToGoogle) {
            int interval = 0;
            int expires_in = 0;
            String device_code = "";
            try {
                interval = receivedJson.getInt("interval");
                expires_in = receivedJson.getInt("expires_in");
                device_code = receivedJson.getString("device_code");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - lastCheckTime >= interval * 1000 && System.currentTimeMillis() - timeReceivedResponseFromGoogle <= expires_in * 1000) {
                lastCheckTime = System.currentTimeMillis();
                Net.HttpRequest r = new Net.HttpRequest(Net.HttpMethods.POST);
                r.setHeader(HttpRequestHeader.ContentType, "application/x-www-form-urlencoded");
                r.setUrl("https://www.googleapis.com/oauth2/v4/token");
                r.setContent("client_id=775862664818-4ejmh7ap6nkvjethc6k0l7ond182bj01.apps.googleusercontent.com&client_secret=pd1V3aECLVS58ejFwWElyT9o&code=" + device_code + "&grant_type=http://oauth.net/grant_type/device/1.0");

                Gdx.net.sendHttpRequest(r, new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        try {
                            JSONObject object = new JSONObject(httpResponse.getResultAsString().trim());
                            if (!object.has("error")) {
                                successfullyLoggedIn = true;
                                String access_token = object.getString("access_token");
                                String token_type = object.getString("token_type");
                                String refresh_token = object.getString("refresh_token");
                                String id_token = object.getString("id_token");

                                PixelShooter.googleAccount = new GoogleAccount(access_token, token_type, refresh_token, id_token);

                                getButtonByID(BUTTON_LOGIN).setShown(false);

                                PSTagCompound tag = new PSTagCompound();
                                PixelShooter.googleAccount.writeToTag(tag);
                                try {
                                    tag.writeToStream(new DataOutputStream(new FileOutputStream("login")));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void cancelled() {

                    }
                });
            }
        }
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.MAIN_MENU);
                break;
            case BUTTON_LOGIN:
                String verification_url = null;
                try {
                    verification_url = receivedJson.getString("verification_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Gdx.net.openURI(verification_url);
                break;
        }
    }

    public void reset() {
        connectedToGoogle = false;
        receivedJson = null;
        failedToConnect = false;
        exception = null;
        successfullyLoggedIn = false;
    }

    @Override
    public int getID() {
        return PixelShooter.CONNECT_TO_GOOGLE;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, 0, width, 100, "Back", BUTTON_BACK, true));
        componentList.add(new GuiButton(width / 2 - 300, height - 120 - PixelShooter.unScaledPerfectPixel32.getLineHeight() * 7 - 100, 600, 100, "Open Browser!", BUTTON_LOGIN, false));
    }
}
