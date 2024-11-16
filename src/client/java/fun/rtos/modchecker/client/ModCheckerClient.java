package fun.rtos.modchecker.client;

import fun.rtos.modchecker.network.client.ClientNetworks;
import net.fabricmc.api.ClientModInitializer;

public class ModCheckerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientNetworks.register();
    }
}
