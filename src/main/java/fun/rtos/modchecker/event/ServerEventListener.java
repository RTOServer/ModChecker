package fun.rtos.modchecker.event;

import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.utils.ScheduleExecutor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerEventListener {
    public static void register() {
        ServerLifecycleEvents.BEFORE_SAVE.register((server1, flush, force) -> ModChecker.CONFIG.saveConfig());
        ServerLifecycleEvents.SERVER_STARTING.register(server -> ScheduleExecutor.server = server);
        ServerTickEvents.END_SERVER_TICK.register(server -> ScheduleExecutor.execute());
    }
}
