package de.intektor.pixelshooter.net.client;

import com.badlogic.gdx.Gdx;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter_common.common.Side;
import de.intektor.pixelshooter_common.net.packet.ClientVersionPacketToServer;
import de.intektor.pixelshooter_common.packet.Packet;
import de.intektor.pixelshooter_common.packet.PacketHelper;
import de.intektor.pixelshooter_common.packet.PacketRegistry;

import javax.net.ssl.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;

/**
 * A client that connects to the main pixelshooter server
 *
 * @author Intektor
 */
public class MainServerClient {

    public volatile SSLSocket connection;

    public MainServerClient() {
    }

    public Socket run() throws IOException {
        connection = (SSLSocket) getSSLContext().getSocketFactory().createSocket("84.200.24.127", 22198);
        connection.setSoTimeout(0);
        PacketHelper.sendPacket(new ClientVersionPacketToServer(PixelShooter.VERSION), connection);
        new Thread() {
            @Override
            public void run() {
                boolean active = true;
                while (PixelShooter.gameRunning && active) {
                    try {
                        Packet packet = PacketHelper.readPacket(new DataInputStream(connection.getInputStream()));
                        PacketRegistry.INSTANCE.getHandlerForPacketClass(packet.getClass()).newInstance().handlePacket(packet, connection, Side.SERVER);
                    } catch (Exception e) {
                        e.printStackTrace();
                        active = false;
                    }
                }
            }
        }.start();
        return connection;
    }

    public static SSLContext getSSLContext() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLS");
            String defaultType = KeyStore.getDefaultType();
            KeyStore ks = null;
            if (defaultType.equalsIgnoreCase("bks")) {
                ks = KeyStore.getInstance("bks");
                ks.load(Gdx.files.local("assets/keystore/serversidekeystore.bks").read(), "HartesPasswort123$$".toCharArray());
            } else if (defaultType.equalsIgnoreCase("jks")) {
                ks = KeyStore.getInstance("jks");
                ks.load(Gdx.files.local("assets/keystore/serversidestore.jks").read(), "HartesPasswort123$$".toCharArray());
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, "HartesPasswort123$$".toCharArray());
            sslContext.init(kmf.getKeyManagers(), trustAllCerts, null);
            return sslContext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
