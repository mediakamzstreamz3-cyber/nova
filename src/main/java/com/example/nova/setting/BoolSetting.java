package com.example.nova.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BoolSetting extends Setting {
    public boolean value;

    public BoolSetting(String name, boolean def) {
        super(name);
        this.value = def;
    }

    public void toggle() { value = !value; }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override public void fromJson(JsonElement e) {
        if (e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean()) value = e.getAsBoolean();
    }
}
