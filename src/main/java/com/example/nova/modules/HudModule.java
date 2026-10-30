package com.example.nova.modules;

import com.example.nova.module.Module;
import com.example.nova.setting.BoolSetting;
import com.example.nova.setting.NumSetting;

public class HudModule extends Module {
    public final BoolSetting watermark = add(new BoolSetting("Watermark", true));
    public final BoolSetting fps = add(new BoolSetting("FPS", true));
    public final BoolSetting coords = add(new BoolSetting("Coordinates", true));
    public final BoolSetting arrayList = add(new BoolSetting("Module list", true));
    public final BoolSetting shadow = add(new BoolSetting("Text shadow", true));
    public final NumSetting rainbowSpeed = add(new NumSetting("Rainbow speed", 3, 1, 10, 1));

    public HudModule() {
        super("HUD", "On-screen info and module list.", -1);
    }
}
