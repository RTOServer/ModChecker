package fun.rtos.modchecker.utils;

import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class ScheduleExecutor {
    public static MinecraftServer server = null;
    public static final SortedMap<Long, Set<BiConsumer<Long, MinecraftServer>>> TASKS = new TreeMap<>();

    public static void schedule(long delay, BiConsumer<Long, MinecraftServer> task) {
        if (ScheduleExecutor.server == null) return;
        long curTime = ScheduleExecutor.server.overworld().getGameTime();
        ScheduleExecutor.TASKS.computeIfAbsent(curTime + delay, d -> new HashSet<>()).add(task);
    }

    public static void execute() {
        if (ScheduleExecutor.server == null) return;
        if (ScheduleExecutor.TASKS.isEmpty()) return;
        long curTime = ScheduleExecutor.server.overworld().getGameTime();
        long executeTime = ScheduleExecutor.TASKS.firstKey();
        if (executeTime > curTime) return;
        TASKS.remove(executeTime).forEach(task -> task.accept(curTime, ScheduleExecutor.server));
    }
}
