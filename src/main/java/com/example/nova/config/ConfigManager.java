package com.example.nova.config;

import com.example.nova.NovaClient;
import com.example.nova.module.Module;
import com.example.nova.setting.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Saves everything to .minecraft/config/nova.json */
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("nova.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : NovaClient.modules.all()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                o.addProperty("key", m.key);
                JsonObject s = new JsonObject();
                for (Setting st : m.settings) s.add(st.name, st.toJson());
                o.add("settings", s);
                root.add(m.name, o);
            }
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            System.err.println("[Nova] Failed to save config: " + e);
        }
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) { save(); return; }
        try {
            JsonObject root = GSON.fromJson(Files.readString(f), JsonObject.class);
            if (root == null) return;
            for (Module m : NovaClient.modules.all()) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("key")) m.key = o.get("key").getAsInt();
                if (o.has("settings")) {
                    JsonObject s = o.getAsJsonObject("settings");
                    for (Setting st : m.settings) if (s.has(st.name)) st.fromJson(s.get(st.name));
                }
                if (o.has("enabled")) m.setEnabled(o.get("enabled").getAsBoolean());
            }
        } catch (Exception e) {
            System.err.println("[Nova] Failed to load config: " + e);
        }
    }
}
