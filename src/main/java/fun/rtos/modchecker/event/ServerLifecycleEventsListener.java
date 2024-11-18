package fun.rtos.modchecker.event;

import fun.rtos.modchecker.ModChecker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerLifecycleEventsListener {
    public static void register() {
        ServerLifecycleEvents.BEFORE_SAVE.register((server1, flush, force) -> ModChecker.CONFIG.saveConfig());
    }
}
