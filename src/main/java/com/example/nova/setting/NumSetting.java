package com.example.nova.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumSetting extends Setting {
    public final double min, max, step;
    public double value;

    public NumSetting(String name, double def, double min, double max, double step) {
        super(name);
        this.value = def;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        value = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, value));
    }

    public void setFromFraction(double f) {
        f = Math.max(0, Math.min(1, f));
        set(min + f * (max - min));
    }

    public double fraction() { return (value - min) / (max - min); }
    public int intValue() { return (int) Math.round(value); }
    public float floatValue() { return (float) value; }

    public String display() {
        return step >= 1 ? String.valueOf(intValue()) : String.format("%.2f", value);
    }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override public void fromJson(JsonElement e) {
        if (e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) set(e.getAsDouble());
    }
}
