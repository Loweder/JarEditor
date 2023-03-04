package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.utils.MyCloneable;

import java.util.Arrays;

public class PackageInfo implements MyCloneable {
    public String[] name;

    public PackageInfo(String in) {
        name = in.split("\\.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageInfo that = (PackageInfo) o;
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
    public PackageInfo clone() {
        try {
            PackageInfo clone = (PackageInfo) super.clone();
            clone.name = name.clone();
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
