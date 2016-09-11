package de.intektor.pixelshooter.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestHeader;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.jackson2.JacksonFactory;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Intektor
 */
public class GoogleAccount {

    public String accessToken;
    public String tokenType;
    public String refreshToken;
    public String idToken;

    public GoogleAccount() {
    }

    public GoogleAccount(String accessToken, String tokenType, String refreshToken, String idToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = refreshToken;
        this.idToken = idToken;
    }

    public void writeToTag(PSTagCompound tag) {
        tag.setString("accessToken", accessToken);
        tag.setString("tokenType", tokenType);
        tag.setString("refreshToken", refreshToken);
        tag.setString("idToken", idToken);
    }

    public void readFromTag(PSTagCompound tag) {
        accessToken = tag.getString("accessToken");
        tokenType = tag.getString("tokenType");
        refreshToken = tag.getString("refreshToken");
        idToken = tag.getString("idToken");
    }

    public String getEmail() {
        GoogleIdToken idToken = null;
        try {
            idToken = GoogleIdToken.parse(new JacksonFactory(), this.idToken);
            return idToken.getPayload().getEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void refreshToken() {
        Net.HttpRequest r = new Net.HttpRequest(Net.HttpMethods.POST);
        r.setHeader(HttpRequestHeader.ContentType, "application/x-www-form-urlencoded");
        r.setUrl("https://www.googleapis.com/oauth2/v4/token");
        r.setContent("client_id=775862664818-4ejmh7ap6nkvjethc6k0l7ond182bj01.apps.googleusercontent.com&" +
                "client_secret=pd1V3aECLVS58ejFwWElyT9o&" +
                "refreshToken=" + refreshToken + "&" +
                "grant_type=refreshToken");

        Gdx.net.sendHttpRequest(r, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                try {
                    JSONObject object = new JSONObject(httpResponse.getResultAsString().trim());
                    if (!object.has("error")) {
                        accessToken = object.getString("accessToken");
                        idToken = object.getString("idToken");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
