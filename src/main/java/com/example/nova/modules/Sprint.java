package com.example.nova.modules;

import com.example.nova.module.Module;
import com.example.nova.setting.BoolSetting;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
    private final BoolSetting hungerCheck = add(new BoolSetting("Hunger check", true));
    private final BoolSetting whileSneaking = add(new BoolSetting("While sneaking", false));

    public Sprint() {
        super("Sprint", "Always sprint while moving forward.", GLFW.GLFW_KEY_R);
    }

    @Override
    public void onTick() {
        if (mc.player.horizontalCollision) return;
        if (!mc.options.forwardKey.isPressed()) return;
        if (mc.player.isSneaking() && !whileSneaking.value) return;
        if (hungerCheck.value && mc.player.getHungerManager().getFoodLevel() <= 6) return;
        mc.player.setSprinting(true);
    }
}
