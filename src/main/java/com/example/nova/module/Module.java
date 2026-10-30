package com.example.nova.module;

import com.example.nova.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    public final String name;
    public final String description;
    public int key; // GLFW key code, -1 = unbound
    public final List<Setting> settings = new ArrayList<>();
    private boolean enabled;

    public Module(String name, String description, int key) {
        this.name = name;
        this.description = description;
        this.key = key;
    }

    protected <T extends Setting> T add(T s) {
        settings.add(s);
        return s;
    }

    public boolean isEnabled() { return enabled; }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (enabled) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    /** Runs every client tick while enabled and in a world. */
    public void onTick() {}
}
