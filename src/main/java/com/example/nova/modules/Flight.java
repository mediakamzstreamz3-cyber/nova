package com.example.nova.modules;

import com.example.nova.module.Module;
import com.example.nova.setting.NumSetting;
import org.lwjgl.glfw.GLFW;

public class Flight extends Module {
    private final NumSetting speed = add(new NumSetting("Speed", 0.10, 0.05, 1.0, 0.05));

    public Flight() {
        super("Flight", "Creative-style flight (most servers block this).", GLFW.GLFW_KEY_G);
    }

    @Override
    public void onTick() {
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().setFlySpeed(speed.floatValue());
    }

    @Override
    public void onDisable() {
        if (mc.player == null) return;
        var a = mc.player.getAbilities();
        a.setFlySpeed(0.05f);
        if (!mc.player.isCreative() && !mc.player.isSpectator()) {
            a.allowFlying = false;
            a.flying = false;
        }
    }
}
