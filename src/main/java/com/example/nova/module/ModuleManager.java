package com.example.nova.module;

import com.example.nova.modules.Flight;
import com.example.nova.modules.Fullbright;
import com.example.nova.modules.HudModule;
import com.example.nova.modules.Sprint;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private final Set<Integer> heldKeys = new HashSet<>();
    public HudModule hud;

    public void init() {
        hud = new HudModule();
        hud.setEnabled(true); // on by default; saved config can override

        register(hud);
        register(new Sprint());
        register(new Fullbright());
        register(new Flight());
        // Add your modules here
    }

    public void register(Module m) { modules.add(m); }

    public List<Module> all() { return modules; }

    public void tick(MinecraftClient mc) {
        if (mc.getWindow() == null) return;

        if (mc.currentScreen == null) {
            for (Module m : modules) {
                if (m.key <= 0) continue;
                boolean down = InputUtil.isKeyPressed(mc.getWindow().getHandle(), m.key);
                if (down && heldKeys.add(m.key)) m.toggle();
                else if (!down) heldKeys.remove(m.key);
            }
        }

        if (mc.player == null || mc.world == null) return;
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick();
        }
    }
}
