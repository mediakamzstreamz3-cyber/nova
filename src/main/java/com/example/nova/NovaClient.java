package com.example.nova;

import com.example.nova.config.ConfigManager;
import com.example.nova.gui.ClickGui;
import com.example.nova.hud.Hud;
import com.example.nova.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class NovaClient implements ClientModInitializer {
    public static final String NAME = "Nova";
    public static ModuleManager modules;

    private boolean guiKeyWasDown = false;

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        modules.init();
        ConfigManager.load();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            handleGuiKey(mc);
            modules.tick(mc);
        });

        HudRenderCallback.EVENT.register(Hud::render);
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> ConfigManager.save());
    }

    // RIGHT SHIFT opens the click GUI
    private void handleGuiKey(MinecraftClient mc) {
        if (mc.getWindow() == null) return;
        boolean down = InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT);
        if (down && !guiKeyWasDown && mc.currentScreen == null) {
            mc.setScreen(new ClickGui());
        }
        guiKeyWasDown = down;
    }
}
