package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.utils.MyCloneable;

import java.util.Arrays;
import java.util.Objects;

public class ClassInfo implements MyCloneable {
    public String name;
    public String[] pkg;

    public ClassInfo(String in) {
        if (in == null) {
            name = "";
            pkg = null;
            return;
        }
        in = in.replace('.', '/');
        String[] all = in.split("/");
        if (all.length != 0) {
            name = all[all.length - 1];
            pkg = all.length == 1 ? null : Arrays.copyOfRange(all, 0, all.length - 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfo classInfo = (ClassInfo) o;
        return Objects.equals(name, classInfo.name) && Arrays.equals(pkg, classInfo.pkg);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(pkg);
        return result;
    }

    public String toRaw() {
        return pkg != null ? String.join("/", pkg) + "/" + name : name;
    }

    @Override
    public ClassInfo clone() {
        try {
            ClassInfo newObject = (ClassInfo) super.clone();
            newObject.name = name;
            newObject.pkg = pkg.clone();
            return newObject;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
