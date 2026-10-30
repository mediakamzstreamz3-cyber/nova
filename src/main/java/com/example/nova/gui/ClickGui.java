package com.example.nova.gui;

import com.example.nova.NovaClient;
import com.example.nova.config.ConfigManager;
import com.example.nova.module.Module;
import com.example.nova.setting.BoolSetting;
import com.example.nova.setting.NumSetting;
import com.example.nova.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Left click module = toggle | Right click = show settings | Middle click = set keybind
 * (then press a key; Backspace/Delete clears it, Escape cancels)
 */
public class ClickGui extends Screen {
    private static final int X = 20, Y = 20, W = 160, H = 16;
    private static final Set<Module> expanded = new HashSet<>();

    private record Row(Module module, Setting setting, int y) {}

    private Module binding;
    private NumSetting dragging;

    public ClickGui() {
        super(Text.literal("Nova"));
    }

    private List<Row> rows() {
        List<Row> rows = new ArrayList<>();
        int y = Y + H;
        for (Module m : NovaClient.modules.all()) {
            rows.add(new Row(m, null, y));
            y += H;
            if (expanded.contains(m)) {
                for (Setting s : m.settings) {
                    rows.add(new Row(m, s, y));
                    y += H;
                }
            }
        }
        return rows;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(X, Y, X + W, Y + H, 0xFF5B3FD6);
        ctx.drawTextWithShadow(textRenderer, "Nova", X + 4, Y + 4, 0xFFFFFFFF);

        String tooltip = null;
        for (Row r : rows()) {
            boolean hover = mouseX >= X && mouseX < X + W && mouseY >= r.y() && mouseY < r.y() + H;

            if (r.setting() == null) {
                Module m = r.module();
                int bg = m.isEnabled() ? 0xFF3A2A8C : (hover ? 0xFF2A2A2A : 0xFF171717);
                ctx.fill(X, r.y(), X + W, r.y() + H, bg);
                ctx.drawTextWithShadow(textRenderer, m.name, X + 4, r.y() + 4, 0xFFFFFFFF);

                String right;
                if (binding == m) right = "[press key]";
                else if (m.key > 0) right = "[" + InputUtil.fromKeyCode(m.key, -1).getLocalizedText().getString() + "]";
                else right = m.settings.isEmpty() ? "" : (expanded.contains(m) ? "-" : "+");
                ctx.drawTextWithShadow(textRenderer, right, X + W - textRenderer.getWidth(right) - 4, r.y() + 4, 0xFFAAAAAA);
                if (hover) tooltip = m.description;
            } else {
                Setting s = r.setting();
                ctx.fill(X, r.y(), X + W, r.y() + H, 0xFF0F0F0F);
                if (s instanceof BoolSetting b) {
                    ctx.drawTextWithShadow(textRenderer, "  " + b.name, X + 4, r.y() + 4, 0xFFDDDDDD);
                    String v = b.value ? "ON" : "OFF";
                    ctx.drawTextWithShadow(textRenderer, v, X + W - textRenderer.getWidth(v) - 4, r.y() + 4,
                            b.value ? 0xFF66FF88 : 0xFFFF6677);
                } else if (s instanceof NumSetting n) {
                    int fillW = (int) ((W - 6) * n.fraction());
                    ctx.fill(X + 3, r.y() + H - 4, X + 3 + (W - 6), r.y() + H - 2, 0xFF2A2A2A);
                    ctx.fill(X + 3, r.y() + H - 4, X + 3 + fillW, r.y() + H - 2, 0xFF7B5FF6);
                    ctx.drawTextWithShadow(textRenderer, "  " + n.name, X + 4, r.y() + 2, 0xFFDDDDDD);
                    String v = n.display();
                    ctx.drawTextWithShadow(textRenderer, v, X + W - textRenderer.getWidth(v) - 4, r.y() + 2, 0xFFFFFFFF);
                }
            }
        }
        if (tooltip != null) ctx.drawTextWithShadow(textRenderer, tooltip, mouseX + 8, mouseY, 0xFFAAAAAA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (binding != null) { binding = null; return true; }

        for (Row r : rows()) {
            if (mouseX < X || mouseX >= X + W || mouseY < r.y() || mouseY >= r.y() + H) continue;

            if (r.setting() == null) {
                Module m = r.module();
                if (button == 0) m.toggle();
                else if (button == 1 && !m.settings.isEmpty()) {
                    if (!expanded.remove(m)) expanded.add(m);
                } else if (button == 2) binding = m;
            } else if (button == 0) {
                if (r.setting() instanceof BoolSetting b) b.toggle();
                else if (r.setting() instanceof NumSetting n) {
                    dragging = n;
                    n.setFromFraction((mouseX - X - 3) / (W - 6));
                }
            }
            ConfigManager.save();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging != null) {
            dragging.setFromFraction((mouseX - X - 3) / (W - 6));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) { dragging = null; ConfigManager.save(); }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding != null) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) binding.key = -1;
            else if (keyCode != GLFW.GLFW_KEY_ESCAPE) binding.key = keyCode;
            binding = null;
            ConfigManager.save();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        ConfigManager.save();
        super.close();
    }

    @Override
    public boolean shouldPause() { return false; }
}
