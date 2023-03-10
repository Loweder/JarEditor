package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.utils.IMyCloneable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class ClassInfo implements IMyCloneable {
    public String name;
    public PackageInfo pkg;
    public int dimensions = 0;

    public ClassInfo(String in) {
        if (in == null) {
            name = "";
            pkg = new PackageInfo("");
            return;
        }
        in = in.replace('.', '/');
        while (in.startsWith("[")) {
            dimensions++;
            in = in.substring(1);
        }
        if (dimensions != 0 && in.startsWith("L")) {
            in = in.substring(1, in.length() - 1);
        }
        String[] all = in.split("/");
        if (all.length != 0) {
            name = all[all.length - 1];
            pkg = new PackageInfo(all.length == 1 ? "" : String.join(".", Arrays.copyOfRange(all, 0, all.length - 1)));
        } else {
            name = "";
            pkg = new PackageInfo("");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfo classInfo = (ClassInfo) o;
        return dimensions == classInfo.dimensions && Objects.equals(name, classInfo.name) && Objects.equals(pkg, classInfo.pkg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pkg, dimensions);
    }

    public ClassInfo getInfo() {
        String pkgString = pkg.name == null ? "" : pkg.toRaw().replace('.', '/') + "/";
        String valueString = dimensions == 0 ? pkgString + name : (pkg.name == null ? name : "L" + pkgString + name + ";");
        String arrayPrefix = String.join("", Collections.nCopies(dimensions, "["));
        return new ClassInfo(arrayPrefix + valueString);
    }

    public String toRaw() {
        String pkgString = pkg.name == null ? "" : pkg.toRaw().replace('.', '/') + "/";
        String valueString = dimensions == 0 ? pkgString + name : (pkg.name == null ? name : "L" + pkgString + name + ";");
        String arrayPrefix = String.join("", Collections.nCopies(dimensions, "["));
        return arrayPrefix + valueString;
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

    public boolean startsWith(PackageInfo info) {
        return pkg.startsWith(info);
    }
}
