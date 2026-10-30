package com.example.nova.hud;

import com.example.nova.NovaClient;
import com.example.nova.module.Module;
import com.example.nova.modules.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;

public class Hud {
    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HudModule h = NovaClient.modules.hud;
        if (!h.isEnabled() || mc.options.hudHidden) return;
        TextRenderer font = mc.textRenderer;

        int y = 4;
        if (h.watermark.value) {
            text(ctx, font, h, NovaClient.NAME, 4, y, rainbow(h, 0));
            y += font.fontHeight + 2;
        }
        if (h.fps.value) {
            text(ctx, font, h, mc.getCurrentFps() + " FPS", 4, y, 0xFFFFFFFF);
            y += font.fontHeight + 2;
        }
        if (h.coords.value && mc.player != null) {
            String c = "XYZ " + mc.player.getBlockX() + " " + mc.player.getBlockY() + " " + mc.player.getBlockZ();
            text(ctx, font, h, c, 4, y, 0xFFFFFFFF);
        }

        if (h.arrayList.value) {
            List<Module> active = NovaClient.modules.all().stream()
                    .filter(m -> m.isEnabled() && m != h)
                    .sorted(Comparator.comparingInt((Module m) -> font.getWidth(m.name)).reversed())
                    .toList();
            int screenW = ctx.getScaledWindowWidth();
            int ry = 4, i = 0;
            for (Module m : active) {
                text(ctx, font, h, m.name, screenW - font.getWidth(m.name) - 4, ry, rainbow(h, i * 150));
                ry += font.fontHeight + 2;
                i++;
            }
        }
    }

    private static void text(DrawContext ctx, TextRenderer font, HudModule h, String s, int x, int y, int color) {
        ctx.drawText(font, s, x, y, color, h.shadow.value);
    }

    private static int rainbow(HudModule h, int offset) {
        float period = 6000f / (float) h.rainbowSpeed.value;
        float hue = ((System.currentTimeMillis() + offset) % (long) period) / period;
        return Color.HSBtoRGB(hue, 0.7f, 1f) | 0xFF000000;
    }
}
