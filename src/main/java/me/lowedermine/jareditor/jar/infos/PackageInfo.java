package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.utils.IMyCloneable;

import java.util.Arrays;
import java.util.Objects;

public class PackageInfo implements IMyCloneable {
    public String[] name;

    public PackageInfo(String in) {
        if (in == null || in.length() == 0) {
            name = null;
            return;
        }
        in = in.replace('.', '/');
        name = in.split("/");
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

    public boolean startsWith(PackageInfo info) {
        if (name == null) return info.name == null;
        if (info.name.length > name.length) return false;
        for (int i = 0; i < info.name.length; i++) {
            if (!Objects.equals(name[i], info.name[i]))
                return false;
        }
        return true;
    }

    public String toRaw() {
        return name == null ? "" : String.join(".", name);
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
