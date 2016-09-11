package de.intektor.pixelshooter.util;

public class TickTimer {

    private int maxTicks;
    private int ticks;
    private String tickerID;
    private boolean active = true;

    public TickTimer(int ticker, String id) {
        maxTicks = ticker;
        tickerID = id;
    }

    public int getCurrentTicks() {
        return ticks;
    }

    public void updateTickTimer() {
        ticks++;
    }

    public boolean hasReachedTickTimer() {
        if (ticks >= maxTicks) {
            return true;
        }
        return false;
    }

    public String getID() {
        return tickerID;
    }

    public void reset() {
        ticks = 0;
    }

    public void reset(int maxTicks) {
        this.maxTicks = maxTicks;
        ticks = 0;
    }

    public void setActive(boolean flag) {
        active = flag;
    }

    public boolean isActive() {
        return active;
    }

    public int getMaxTicks() {
        return maxTicks;
    }
}
