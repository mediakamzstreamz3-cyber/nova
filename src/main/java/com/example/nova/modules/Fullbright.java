package com.example.nova.modules;

import com.example.nova.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "See in the dark.", GLFW.GLFW_KEY_B);
    }

    @Override
    public void onTick() {
        mc.player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NIGHT_VISION, 1000, 0, false, false));
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
