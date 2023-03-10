package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.PackageInfo;

import java.io.*;
import java.util.Objects;

public class PackageConstant implements IConstant {
    public int name;

    public PackageConstant() {

    }

    public PackageConstant(DataInputStream in) throws IOException {
        name = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(20);
        out.writeShort(name);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new PackageInfo((String) cp.rc(name).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageConstant that = (PackageConstant) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
