
package de.intektor.pixelshooter.game_state.community_levels.browse_user_levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.intektor.pixelshooter.PixelShooter;
import de.intektor.pixelshooter.abstrct.AbstractHelper;
import de.intektor.pixelshooter.abstrct.ImageStorage;
import de.intektor.pixelshooter.auth.GoogleAccount;
import de.intektor.pixelshooter.gui.Gui;
import de.intektor.pixelshooter.gui.GuiButton;
import de.intektor.pixelshooter.gui.GuiScrollBar;
import de.intektor.pixelshooter.gui.GuiScrollBar.Direction;
import de.intektor.pixelshooter.gui.text_field.GuiTextField;
import de.intektor.pixelshooter.render.RenderHelper;
import de.intektor.pixelshooter.util.StringUtils;
import de.intektor.pixelshooter.util.TickTimerHandler;
import de.intektor.pixelshooter_common.files.pstf.PSTagCompound;
import de.intektor.pixelshooter_common.levels.BasicLevelInformation;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelRequestResponseToClient.SupplyType;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelsLevelRequestToServer;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelsLevelRequestToServer.Function;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelsLevelRequestToServer.Order;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelsLevelRequestToServer.Order.OrderType;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelsLevelRequestToServer.Time;
import de.intektor.pixelshooter_common.net.packet.BrowseCommunityLevelsLevelRequestToServer.Type;
import de.intektor.pixelshooter_common.net.packet.RequestLevelDataPacketToServer;
import de.intektor.pixelshooter_common.packet.PacketHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Intektor
 */
public class GuiBrowseCommunityLevelsFromMainServer extends Gui {

    final int BUTTON_BACK = 0, BUTTON_FILTER_TIME = 1, BUTTON_SEARCH = 2,
            BUTTON_FILTER_TYPE = 3, BUTTON_REFRESH = 4, BUTTON_CANCEL_SEARCH = 5,
            BUTTON_USER_LEVELS = 6, BUTTON_NORMAL_LEVELS = 7, BUTTON_OFFICIAL_ID = 8;

    final String TICK_TIMER_BROWSE_COMMUNITY_LEVELS_REQUEST_MORE_STRING = "TICK_TIMER_BROWSE_COMMUNITY_LEVELS_REQUEST_MORE_STRING";
    int currentBrowseStringTick;
    GuiScrollBar scrollBar;

    final int TEXT_FIELD_SEARCH = 0, TEXT_FIELD_USER_FILTER = 1;
    GuiTextField textFieldSearch;
    GuiTextField textFieldUserFilter;

    boolean wasHereBefore;

    List<BasicLevelInformation> levels = new ArrayList<BasicLevelInformation>();

    SupplyType supply;

    boolean sendMoreRequestPacket;

    int amtOfButtons;
    int basicAmtOfComponents;

    Time timeOptionAtLastRefresh;
    Type typeOptionAtLastRefresh;

    String searchStringAtLastSearch = "";
    String userStringAtLastSearch = "";

    boolean userLevels;

    int buttonHeight;

    public GuiBrowseCommunityLevelsFromMainServer() {
        TickTimerHandler.registerTickTimer(30, TICK_TIMER_BROWSE_COMMUNITY_LEVELS_REQUEST_MORE_STRING);
        buttonHeight = AbstractHelper.isTouchDevice() ? 75 : 50;
    }

    @Override
    public void enterGui() {
        super.enterGui();
        refresh();
        if (!wasHereBefore) {
            wasHereBefore = true;
        }
        userLevels = false;
    }

    @Override
    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(ImageStorage.main_menu_wooden, 0, 0, width, height);
        batch.end();
        super.render(renderer, batch);
        renderer.begin();

        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLACK);
        renderer.rect(0, height - buttonHeight * 2 - 50, width, 50);

        renderer.set(ShapeRenderer.ShapeType.Line);

        renderer.setColor(Color.DARK_GRAY);

        renderer.line(0, height - buttonHeight * 2 - 50, width, height - buttonHeight * 2 - 50);

        renderer.identity();

        float percent = -scrollBar.getScrollPercent();
        int scrollAmount = (int) ((levels.size() * buttonHeight - height + buttonHeight * 3 + buttonHeight * 2) * percent);

        int y2 = height - buttonHeight * 2 - 50 - levels.size() * buttonHeight - scrollAmount;

        renderer.line(0, height - buttonHeight * 2, width, height - buttonHeight * 2);
        renderer.identity();
        renderer.line(width / 4, height - buttonHeight * 2, width / 4, y2);
        renderer.identity();
        renderer.line(width / 2, height - buttonHeight * 2, width / 2, y2);
        renderer.identity();
        renderer.line(width / 2 + width / 2 / 4, height - buttonHeight * 2, width / 2 + width / 2 / 4, y2);
        renderer.identity();
        renderer.line(width / 2 + width / 2 / 4 * 2, height - buttonHeight * 2, width / 2 + width / 2 / 4 * 2, y2);
        renderer.identity();
        renderer.line(width / 2 + width / 2 / 4 * 3, height - buttonHeight * 2, width / 2 + width / 2 / 4 * 3, y2);
        renderer.identity();
        renderer.line(width - 75, height - buttonHeight * 2, width - 75, y2);
        renderer.end();

        BitmapFont font = PixelShooter.unScaledPerfectPixel22;
        batch.begin();

        font.setColor(new Color(0xFFFF66FF));

        int i1 = buttonHeight * 2 + 25;
        RenderHelper.drawString(width / 8, height - i1, "Level Name", font, batch);
        RenderHelper.drawString(width / 4 + width / 8, height - i1, "Author", font, batch);
        RenderHelper.drawString(width / 2 + width / 8 / 2, height - i1, "Rating", font, batch);
        RenderHelper.drawString(width / 2 + width / 8 + width / 16, height - i1, "Play Count", font, batch);
        RenderHelper.drawString(width / 2 + width / 4 + width / 16, height - i1, "Downloads", font, batch);
        RenderHelper.drawString(width / 2 + width / 4 + width / 8 + width / 16 / 2, height - i1, "Date", font, batch);

        font.setColor(Color.WHITE);

        batch.end();

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        batch.begin();

        font.setColor(new Color(0x898989ff));
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
        for (int i = 0; i < levels.size(); i++) {
            BasicLevelInformation level = levels.get(i);
            int y = height - buttonHeight * 3 - (scrollAmount + i * buttonHeight) + buttonHeight / 2 - 50;
            boolean bool = y < height - buttonHeight * 2 && y > -buttonHeight;
            if (bool) {
                RenderHelper.drawString(10, y, level.levelName, font, batch, false, true);
                RenderHelper.drawString(width / 4 + 10, y, level.authorName, font, batch, false, true);
                batch.draw(ImageStorage.empty_stars, width / 2 + width / 8 / 2 - width / 16, y - 15, width / 8, 30);

                float ratio = level.rating / 5;
                batch.draw(new TextureRegion(ImageStorage.full_stars, 0, 0, ratio, 1), width / 2 + width / 8 / 2 - width / 16, y - 15, width / 8 * ratio, 30);

                RenderHelper.drawString(width / 2 + width / 8 + 10, y, level.playCount + "", font, batch, false, true);
                RenderHelper.drawString(width / 2 + width / 4 + 10, y, level.downloadCount + "", font, batch, false, true);
                RenderHelper.drawString(width / 2 + width / 4 + width / 8 + 5, y, format.format(new Date(level.timeUploaded)) + "", font, batch, false, true);
            }
        }
        font.setColor(Color.WHITE);
        batch.end();
        Gdx.gl.glScissor(0, 0, (int) (width * (Gdx.graphics.getWidth() / 1280f)), (int) ((height - buttonHeight * 3) * (Gdx.graphics.getHeight() / 720f) + 50));
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        batch.begin();

        String shown_string = "";

        if (supply == SupplyType.MORE) {
            shown_string = "Requesting more levels" + StringUtils.repeat(".", currentBrowseStringTick);
        } else if (supply == SupplyType.LIMIT) {
            shown_string = "No more levels available!";
        }

        RenderHelper.drawString((width - 75) / 2, y2 - buttonHeight, shown_string, font, batch, true);
        batch.end();
    }

    @Override
    public void update() {
        super.update();

        float percent = -scrollBar.getScrollPercent();

        int scrollAmount = (int) ((levels.size() * buttonHeight - height + buttonHeight * 3 + buttonHeight * 2) * percent);

        if (componentList.size() > 6) {
            for (int i = 0; i < levels.size(); i++) {
                GuiButton button = (GuiButton) componentList.get(i + basicAmtOfComponents + 1);
                button.setY(height - buttonHeight * 3 - (scrollAmount + i * buttonHeight) - 50);
                boolean b = button.getY() < height - buttonHeight * 3 && button.getY() > -buttonHeight;
                button.enabled = b;
                button.setShown(b);
            }
        }

        if (TickTimerHandler.hasTickTimerFinished(TICK_TIMER_BROWSE_COMMUNITY_LEVELS_REQUEST_MORE_STRING)) {
            TickTimerHandler.resetTickTimer(TICK_TIMER_BROWSE_COMMUNITY_LEVELS_REQUEST_MORE_STRING);
            currentBrowseStringTick++;
            if (currentBrowseStringTick >= 4) {
                currentBrowseStringTick = 0;
            }
        }
        if (supply == SupplyType.MORE && !sendMoreRequestPacket) {
            if (scrollAmount < -(levels.size() * buttonHeight - height + buttonHeight * 3)) {
                Function function = textFieldSearch.isShown() ? Function.SEARCH : Function.NORMAL;
                BrowseCommunityLevelsLevelRequestToServer packet = new BrowseCommunityLevelsLevelRequestToServer(getTime(), getType(), function, new Order(OrderType.MORE, levels.get(levels.size() - 1).officialID, textFieldSearch.convertText(), textFieldUserFilter.convertText()), getUserData());
                PacketHelper.sendPacket(packet, PixelShooter.mainServerClient.connection);
                sendMoreRequestPacket = true;
            }
        }
        getButtonByID(BUTTON_SEARCH).enabled = !textFieldSearch.isShown() || !textFieldSearch.convertText().equals(searchStringAtLastSearch) || !textFieldUserFilter.convertText().equals(userStringAtLastSearch);
        getButtonByID(BUTTON_REFRESH).enabled = getTime() != timeOptionAtLastRefresh || getType() != typeOptionAtLastRefresh;
    }

    @Override
    public void onButtonTouched(int id) {
        switch (id) {
            case BUTTON_BACK:
                PixelShooter.enterGui(PixelShooter.BASIC_LEVEL_OVERVIEW);
                try {
                    PixelShooter.mainServerClient.connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case BUTTON_SEARCH:
                getButtonByID(BUTTON_CANCEL_SEARCH).setShown(true);
                getButtonByID(BUTTON_BACK).setShown(false);
                getButtonByID(BUTTON_USER_LEVELS).setShown(false);
                getButtonByID(BUTTON_NORMAL_LEVELS).setShown(false);
                getButtonByID(BUTTON_OFFICIAL_ID).setShown(false);
                if (textFieldSearch.isShown()) {
                    searchStringAtLastSearch = textFieldSearch.convertText();
                    userStringAtLastSearch = textFieldUserFilter.convertText();
                    refresh();
                } else {
                    textFieldSearch.setShown(true);
                    textFieldUserFilter.setShown(true);
                }
                break;
            case BUTTON_CANCEL_SEARCH:
                getButtonByID(BUTTON_CANCEL_SEARCH).setShown(false);
                getButtonByID(BUTTON_BACK).setShown(true);
                getButtonByID(BUTTON_OFFICIAL_ID).setShown(true);
                if (userLevels) {
                    getButtonByID(BUTTON_NORMAL_LEVELS).setShown(true);
                } else {
                    getButtonByID(BUTTON_USER_LEVELS).setShown(true);
                }
                textFieldSearch.setText("");
                textFieldSearch.setShown(false);
                textFieldSearch.setText("");
                textFieldUserFilter.setShown(false);
                refresh();
                break;
            case BUTTON_REFRESH:
                refresh();
                break;
            case BUTTON_USER_LEVELS:
                PixelShooter.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        userLevels = true;
                        getButtonByID(BUTTON_NORMAL_LEVELS).setShown(true);
                        getButtonByID(BUTTON_USER_LEVELS).setShown(false);
                        getButtonByID(BUTTON_SEARCH).setShown(false);
                        getButtonByID(BUTTON_OFFICIAL_ID).setShown(false);
                        refresh();
                    }
                });
                break;
            case BUTTON_NORMAL_LEVELS:
                PixelShooter.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        getButtonByID(BUTTON_NORMAL_LEVELS).setShown(false);
                        getButtonByID(BUTTON_USER_LEVELS).setShown(true);
                        getButtonByID(BUTTON_SEARCH).setShown(true);
                        getButtonByID(BUTTON_OFFICIAL_ID).setShown(true);
                        userLevels = false;
                        refresh();
                    }
                });
                break;
            case BUTTON_OFFICIAL_ID:
                PixelShooter.enterGui(PixelShooter.LOOKUP_OFFICIAL_ID);
                break;
        }
        if (id >= amtOfButtons) {
            BasicLevelInformation level = levels.get(id - amtOfButtons);
            PixelShooter.enterGui(PixelShooter.WAIT_FOR_SERVER_TO_SEND_LEVEL_DATA);
            PSTagCompound userInfo = new PSTagCompound();
            GoogleAccount googleAccount = PixelShooter.googleAccount;
            userInfo.setBoolean("loggedIn", googleAccount != null);
            if (googleAccount != null) {
                userInfo.setString("idToken", googleAccount.idToken);
            }
            PacketHelper.sendPacket(new RequestLevelDataPacketToServer(level.officialID, userInfo), PixelShooter.mainServerClient.connection);
        }
    }

    @Override
    public int getID() {
        return PixelShooter.BROWSE_COMMUNITY_LEVELS_FROM_MAIN_SERVER;
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    public void refresh() {
        reset();
        timeOptionAtLastRefresh = getTime();
        typeOptionAtLastRefresh = getType();
        Function function = !userLevels ? textFieldSearch.isShown() ? Function.SEARCH : Function.NORMAL : Function.USER_LEVEL;
        BrowseCommunityLevelsLevelRequestToServer packet = new BrowseCommunityLevelsLevelRequestToServer(getTime(), getType(), function, new Order(OrderType.NEW, "", textFieldSearch.convertText(), textFieldUserFilter.convertText()), getUserData());
        PacketHelper.sendPacket(packet, PixelShooter.mainServerClient.connection);
    }

    public Time getTime() {
        Time time = null;
        switch (((GuiButton.GuiButtonSwitch) getButtonByID(BUTTON_FILTER_TIME)).getCurrentState()) {
            case 0:
                time = Time.TODAY;
                break;
            case 1:
                time = Time.LAST_WEEK;
                break;
            case 2:
                time = Time.LAST_MONTH;
                break;
            case 3:
                time = Time.LAST_YEAR;
                break;
            case 4:
                time = Time.ANYTIME;
                break;
        }
        return time;
    }

    public Type getType() {
        Type type = null;
        switch (((GuiButton.GuiButtonSwitch) getButtonByID(BUTTON_FILTER_TYPE)).getCurrentState()) {
            case 0:
                type = Type.BEST_RATED;
                break;
            case 1:
                type = Type.PLAY_COUNT;
                break;
            case 2:
                type = Type.MOST_DOWNLOADS;
                break;
            case 3:
                type = Type.NEWEST;
                break;
            case 4:
                type = Type.OLDEST;
                break;
        }
        return type;
    }

    @Override
    public void addGuiComponents() {
        componentList.add(new GuiButton(0, height - buttonHeight, width / 4, buttonHeight, "Back", BUTTON_BACK, true));
        componentList.add(new GuiButton.GuiButtonSwitch(width / 4, height - buttonHeight, width / 4, buttonHeight, BUTTON_FILTER_TIME, true, Arrays.asList("Filter: Today", "Filter: Last Week", "Filter: Last Month", "Filter: Last Year", "Filter: Anytime"), 4));
        componentList.add(new GuiButton(width / 4 * 3, height - buttonHeight, width / 4, buttonHeight, "Refresh", BUTTON_REFRESH, true));
        componentList.add(new GuiButton(width / 4 * 3, height - buttonHeight * 2, width / 4, buttonHeight, "Search", BUTTON_SEARCH, true));
        componentList.add(new GuiButton(0, height - buttonHeight, width / 4, buttonHeight, "Cancel Search", BUTTON_CANCEL_SEARCH, false));

        componentList.add(new GuiButton.GuiButtonSwitch(width / 2, height - buttonHeight, width / 4, buttonHeight, BUTTON_FILTER_TYPE, true, Arrays.asList("Type: Best rating", "Type: Most played", "Type: Most downloads", "Type: Newest", "Type: Oldest"), 3));

        textFieldSearch = new GuiTextField(width / 4, height - buttonHeight * 2, width / 2, buttonHeight, TEXT_FIELD_SEARCH, false, 20, true, true, false, this, "", "");
        componentList.add(textFieldSearch);
        textFieldUserFilter = new GuiTextField(0, height - buttonHeight * 2, width / 4, buttonHeight, TEXT_FIELD_USER_FILTER, false, 20, true, true, true, this, "", "Author Filter:");
        componentList.add(textFieldUserFilter);
        scrollBar = new GuiScrollBar(width - 75, 0, 75, height - buttonHeight * 3, true, Direction.VERTICAL, 10, height - buttonHeight * 3);

        componentList.add(scrollBar);
        componentList.add(new GuiButton(0, height - buttonHeight * 2, width / 4, buttonHeight, "Your Levels", BUTTON_USER_LEVELS, true));
        componentList.add(new GuiButton(0, height - buttonHeight * 2, width / 4, buttonHeight, "Back to normal levels", BUTTON_NORMAL_LEVELS, false));

        componentList.add(new GuiButton(width / 4, height - buttonHeight * 2, width / 2, buttonHeight, "Enter Level Code", BUTTON_OFFICIAL_ID, true));

        amtOfButtons = 9;
        basicAmtOfComponents = amtOfButtons + 3;

    }

    public void setLevels(List<BasicLevelInformation> levels, Order order, SupplyType supply) {
        boolean hadLevelsBefore = !this.levels.isEmpty();
        switch (order.type) {
            case NEW:
                this.levels = levels;
                break;
            case MORE:
                this.levels.addAll(levels);
                break;
        }
        this.supply = supply;
        sendMoreRequestPacket = false;
        reAddButtons(hadLevelsBefore);
    }

    public void reAddButtons(boolean hadLevelsBefore) {
        int id = amtOfButtons;
        componentList = componentList.subList(0, basicAmtOfComponents);
        componentList.add(scrollBar);
        for (BasicLevelInformation level : levels) {
            componentList.add(new GuiButton(0, 0, width - 75, buttonHeight, "", id++, new Color(0xEFEFEFff), new Color(0xEFEFEFff), new Color(0xffffffff), new Color(0xffffffff), Color.RED, Color.RED, true));
        }
        if (hadLevelsBefore) {
            scrollBar.addAllWindowSize((levels.size() * buttonHeight + buttonHeight) - scrollBar.getAllWindowSize());
            scrollBar.currentlyClicked = false;
        } else {
            scrollBar.setAllWindowSize((levels.size() * buttonHeight + buttonHeight));
        }
    }

    public void reset() {
        currentBrowseStringTick = 0;
        levels.clear();
        supply = null;
        sendMoreRequestPacket = false;
    }

    public static PSTagCompound getUserData() {
        PSTagCompound tag = new PSTagCompound();
        tag.setBoolean("loggedIn", PixelShooter.googleAccount != null);
        if (PixelShooter.googleAccount != null) {
            tag.setString("idToken", PixelShooter.googleAccount.idToken);
        }
        return tag;
    }
}