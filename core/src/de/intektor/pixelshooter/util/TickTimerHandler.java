package de.intektor.pixelshooter.util;

import de.intektor.pixelshooter.util.TickTimer;

import java.util.HashMap;
import java.util.Map;


public class TickTimerHandler {

    private static Map<String, TickTimer> tickMap = new HashMap<String, TickTimer>();

    public static void registerTickTimer(TickTimer timer) {
        tickMap.put(timer.getID(), timer);
    }

    public static void registerTickTimer(int ticks, String id) {
        registerTickTimer(new TickTimer(ticks, id));
    }

    public static void updateTickers() {
        for (TickTimer ticker : tickMap.values()) {
            ticker.updateTickTimer();
        }
    }

    public static TickTimer getTickTimer(String id) {
        return tickMap.get(id);
    }

    public static boolean hasTickTimerFinished(String id) {
        return tickMap.get(id).hasReachedTickTimer();
    }

    public static void resetTickTimer(String id) {
        getTickTimer(id).reset();
    }

    public static void resetTickTimer(String id, int ticks) {
        getTickTimer(id).reset(ticks);
    }

    public static boolean isTimerActive(String id) {
        return tickMap.get(id).isActive();
    }

    public static void setTimerActive(String id, boolean flag) {
        tickMap.get(id).setActive(flag);
    }

    public static void clearTickTimers() {
        tickMap.clear();
    }
}
