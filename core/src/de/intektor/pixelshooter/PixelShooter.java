package de.intektor.pixelshooter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.abstrct.WindowsExplorerComperator;
import de.intektor.pixelshooter.auth.GoogleAccount;
import de.intektor.pixelshooter.game_state.*;
import de.intektor.pixelshooter.game_state.community_levels.GuiConnectToMainServer;
import de.intektor.pixelshooter.game_state.community_levels.GuiPublishLevelToMainServer;
import de.intektor.pixelshooter.game_state.community_levels.GuiServerError;
import de.intektor.pixelshooter.game_state.community_levels.GuiWaitForLevelPublishServerResponse;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.GuiBrowseCommunityLevelsFromMainServer;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.GuiLookupOfficialID;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.GuiViewCommunityLevel;
import de.intektor.pixelshooter.game_state.community_levels.browse_user_levels.GuiWaitForServerToSendLevelInfo;
import de.intektor.pixelshooter.game_state.user_level.GuiFinishLevelToPublishLevel;
import de.intektor.pixelshooter.game_state.user_level.GuiSureDeleteLevel;
import de.intektor.pixelshooter.game_state.user_level.GuiUserLevelsFolder;
import de.intektor.pixelshooter.game_state.user_level.GuiUserLevelsOverview;
import de.intektor.pixelshooter.game_state.worlds.GuiViewCampaignWorld;
import de.intektor.pixelshooter.game_state.worlds.GuiWorldSelection;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.level.editor.GuiLevelEditor;
import de.intektor.pixelshooter.level.editor.GuiSetWorldAttributes;
import de.intektor.pixelshooter.levels.CampaignInformation;
import de.intektor.pixelshooter.net.client.MainServerClient;
import de.intektor.pixelshooter.net.packet.handler.*;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.FontHelper;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter_common.PixelShooterCommon;
import de.intektor.pixelshooter_common.common.ClientVersion;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter_common.net.packet.*;
import de.intektor.pixelshooter_common.packet.PacketRegistry;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class PixelShooter extends ApplicationAdapter {

    public static final ClientVersion VERSION = new ClientVersion(1, 0, 0);

    public static ShapeRenderer shapeRenderer;
    public static int width, height;
    public static float guiScaleAmt = 2;
    public static SpriteBatch spriteBatch;
    public static ModelBatch modelBatch;
    public static GlyphLayout layout;
    public static final ModelBuilder modelBuilder = new ModelBuilder();

    public static BitmapFont unScaledPerfectPixel10;
    public static BitmapFont unScaledPerfectPixel12;
    public static BitmapFont unScaledPerfectPixel16;
    public static BitmapFont unScaledPerfectPixel22;
    public static BitmapFont unScaledPerfectPixel32;
    public static BitmapFont unScaledPerfectPixel64;
    public static BitmapFont unScaledPerfectPixel72;
    public static BitmapFont unScaledPerfectPixel128;

    public static final Comparator<String> NATURAL_SORT = new WindowsExplorerComperator();

    public static final int MAIN_MENU = 0;
    public static final int BASIC_LEVEL_OVERVIEW = 1;
    public static final int VIEW_CAMPAIGN_WORLD = 2;
    public static final int PLAY_STATE = 3;
    public static final int LEVEL_EDITOR = 4;
    public static final int USER_LEVEL_OVERVIEW = 5;
    public static final int USER_LEVELS_FOLDER = 6;
    public static final int OPTIONS = 7;
    public static final int LEVEL_EDITOR_SET_PROPERTIES = 8;
    public static final int WORLD_SELECTION = 9;
    public static final int LE_SET_WORLD_ATTRIBUTES = 10;
    public static final int CONNECT_TO_MAIN_SERVER = 11;
    public static final int SURE_DELETE_FOLDER_FILE = 12;
    public static final int FINISH_LEVEL_TO_PUBLISH_LEVEL = 13;
    public static final int PUBLISH_LEVEL_TO_MAIN_SERVER = 14;
    public static final int CONNECT_TO_GOOGLE = 15;
    public static final int WAIT_FOR_PUBLISH_LEVEL_RESPONSE = 16;
    public static final int BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER = 17;
    public static final int BROWSE_COMMUNITY_LEVELS_VIEW_LEVEL = 18;
    public static final int WAIT_FOR_SERVER_TO_SEND_LEVEL_DATA = 19;
    public static final int LOOKUP_OFFICIAL_ID = 20;
    public static final int SERVER_CONNECTION_SHUTDOWN_MESSAGE = 21;

    public static final GuiPlayState PIXEL_SHOOTER_STATE = new GuiPlayState();
    public static GuiLevelEditor LEVEL_EDITOR_STATE;

    public static GoogleAccount googleAccount;

    public static CampaignInformation campaign;

    public static volatile MainServerClient mainServerClient = new MainServerClient();

    public static volatile boolean gameRunning = true;

    private static int currentGUI;

    private static BiMap<Integer, Gui> guiMap = HashBiMap.create();

    public static volatile Queue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();

    public static UUID playerUUID;

    OrthographicCamera camera;

    Viewport viewport;

    @Override
    public void create() {
        System.out.println("Initializing game...");

        System.out.println("Initialize renderer and batch");

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();

        TickTimerHandler.clearTickTimers();

        System.out.println("Initializing scaled fonts");

        ImageStorage.init();

        System.out.println("Registering Guis");
        registerGui(new GuiMainMenu());
        registerGui(new GuiBasicLevelOverview());
        registerGui(new GuiViewCampaignWorld());
        registerGui(new GuiLevelEditor());
        registerGui(new GuiUserLevelsOverview());
        registerGui(new GuiUserLevelsFolder());
        registerGui(new GuiOptions());
        registerGui(new GuiEnterLevelEditor());
        registerGui(new GuiWorldSelection());
        registerGui(new GuiSetWorldAttributes());
        registerGui(new GuiConnectToMainServer());
        registerGui(new GuiSureDeleteLevel());
        registerGui(new GuiFinishLevelToPublishLevel());
        registerGui(new GuiPublishLevelToMainServer());
        registerGui(new GuiConnectToGoogle());
        registerGui(new GuiWaitForLevelPublishServerResponse());
        registerGui(new GuiBrowseCommunityLevelsFromMainServer());
        registerGui(new GuiWaitForServerToSendLevelInfo());
        registerGui(new GuiViewCommunityLevel());
        registerGui(new GuiLookupOfficialID());
        registerGui(new GuiServerError());

        System.out.println("Initializing Camera");
        camera = new OrthographicCamera();
        System.out.println("Initializing Viewport");
        viewport = new StretchViewport(1280, 720, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        System.out.println("Initializing unscaled fonts");

        System.out.println(Arrays.toString(Gdx.files.internal("assets/").list()));

        URL resource = ImageStorage.class.getResource("ImageStorage.class");
        boolean inJar = resource != null && resource.toString().startsWith("jar");
        String domain = inJar ? "" : "assets/";
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.local(domain + "font/PerfectPixel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        unScaledPerfectPixel10 = fontGenerator.generateFont(parameter);
        parameter.size = 12;
        unScaledPerfectPixel12 = fontGenerator.generateFont(parameter);
        parameter.size = 16;
        unScaledPerfectPixel16 = fontGenerator.generateFont(parameter);
        parameter.size = 22;
        unScaledPerfectPixel22 = fontGenerator.generateFont(parameter);
        parameter.size = 32;
        unScaledPerfectPixel32 = fontGenerator.generateFont(parameter);
        parameter.size = 64;
        unScaledPerfectPixel64 = fontGenerator.generateFont(parameter);
        parameter.size = 72;
        unScaledPerfectPixel72 = fontGenerator.generateFont(parameter);
        parameter.size = 128;
        unScaledPerfectPixel128 = fontGenerator.generateFont(parameter);
        fontGenerator.dispose();

        LEVEL_EDITOR_STATE = new GuiLevelEditor();
        registerGui(LEVEL_EDITOR_STATE);
        registerGui(PIXEL_SHOOTER_STATE);

        System.out.println("Attempting reading campaign info from disk");

        try {
            FileInputStream file = new FileInputStream(Gdx.files.local("campaign.info").file());
            PSTagCompound campaignTag = new PSTagCompound();
            campaignTag.readFromStream(new DataInputStream(file));
            campaign = CampaignInformation.readFromTag(campaignTag);
            System.out.println("Info was found!");
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find a campaign file, either this is the first time starting the game or it was deleted!");
            campaign = new CampaignInformation();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            FileInputStream file = new FileInputStream(Gdx.files.local("user.psinfo").file());
            PSTagCompound tag = new PSTagCompound();
            tag.readFromStream(new DataInputStream(file));
            playerUUID = UUID.fromString(tag.getString("player_uuid"));
        } catch (IOException e) {
            playerUUID = UUID.randomUUID();
            try {
                FileOutputStream out = new FileOutputStream(Gdx.files.local("user.psinfo").file());
                PSTagCompound tag = new PSTagCompound();
                tag.setString("player_uuid", playerUUID.toString());
                tag.writeToStream(new DataOutputStream(out));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        System.out.println("Early initialization successful");

        PixelShooterCommon.init();
        PacketRegistry.INSTANCE.registerHandlerForPacket(BadAccessTokenPacketToClient.class, BadAccessTokenPacketToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(InternalServerErrorWhilePublishingPacketToClient.class, InternalServerErrorWhilePublishingPacketToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(LevelPublishedPacketToClient.class, LevelPublishedPacketToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(BrowseCommunityLevelRequestResponseToClient.class, BrowseCommunityLevelRequestResponseToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(RequestLevelDataPacketResponsePacketToClient.class, RequestLevelDataPacketResponsePacketToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(InternalServerErrorWhileGettingLevelDataPacketToClient.class, InternalServerErrorWhileGettingLevelDataPacketToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(ServerShutdownPacketToClient.class, ServerShutdownPacketToClientHandler.class);
        PacketRegistry.INSTANCE.registerHandlerForPacket(UnsupportedClientVersionPacketToClient.class, UnsupportedClientVersionPacketToClientHandler.class);

        try {
            FileInputStream file = new FileInputStream(Gdx.files.local("login").file());
            PSTagCompound tag = new PSTagCompound();
            tag.readFromStream(new DataInputStream(file));
            googleAccount = new GoogleAccount();
            googleAccount.readFromTag(tag);
            googleAccount.refreshToken();
        } catch (Exception ignored) {
        }

        enterGui(currentGUI);
    }

    long lastTicksMilliSec;
    long lastUpdateCallMilliSec;

    /**
     * Use as update method and render method
     */
    @Override
    public void render() {
        lastUpdateCallMilliSec = System.currentTimeMillis();
        //60 Ticks per second
        if (System.currentTimeMillis() - 16.66666667 >= lastTicksMilliSec) {
            lastTicksMilliSec = System.currentTimeMillis();
            TickTimerHandler.updateTickers();
            updateGame();
        }
        renderGame();
    }

    public void updateGame() {
        Runnable t;
        while ((t = tasks.poll()) != null) {
            t.run();
        }
        getGuiByID(currentGUI).update();
    }


    public void renderGame() {
        camera.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.updateMatrices();
        spriteBatch.setProjectionMatrix(camera.combined);
        //noinspection ConstantConditions
        getGuiByID(currentGUI).render(shapeRenderer, spriteBatch);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        int fps = Gdx.graphics.getFramesPerSecond();
        RenderHelper.drawString(1280 - FontHelper.stringWidth(fps + "", unScaledPerfectPixel16), 720 - FontHelper.stringHeight(fps + "", unScaledPerfectPixel16), fps + "", unScaledPerfectPixel16, spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        PixelShooter.width = width;
        PixelShooter.height = height;

        viewport.update(width, height);

        layout = new GlyphLayout();
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void enterGui(int guiID) {
        currentGUI = guiID;
        //noinspection ConstantConditions
        getGuiByID(guiID).init();
    }

    public static Gui getGuiByID(int id) {
        return guiMap.get(id);
    }

    @Override
    public void dispose() {
        gameRunning = false;
        ImageStorage.dispose();
        try {
            PSTagCompound campaignTag = new PSTagCompound();
            campaign.writeToTag(campaignTag);
            campaignTag.writeToStream(new DataOutputStream(new FileOutputStream(Gdx.files.local("campaign.info").file())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerGui(Gui gui) {
        guiMap.put(gui.getID(), gui);
    }

    public static void addScheduledTask(Runnable task) {
        tasks.offer(task);
    }

}
