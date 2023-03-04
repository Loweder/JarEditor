package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.utils.MyCloneable;

import java.util.Arrays;

public class ModuleInfo implements MyCloneable {
    public String[] name;

    public ModuleInfo(String in) {
        name = in.split("\\.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleInfo that = (ModuleInfo) o;
        return Arrays.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(name);
    }

    public String toRaw() {
        return String.join(".", name);
    }

    @Override
    public ModuleInfo clone() {
        try {
            ModuleInfo clone = (ModuleInfo) super.clone();
            clone.name = name.clone();
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
