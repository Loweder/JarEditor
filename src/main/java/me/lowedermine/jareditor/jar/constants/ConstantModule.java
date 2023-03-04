package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.ModuleInfo;

import java.io.*;
import java.util.Objects;

public class ConstantModule implements IConstant {
    public int name;

    public ConstantModule() {

    }

    public ConstantModule(DataInputStream in) throws IOException {
        name = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(19);
        out.writeShort(name);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new ModuleInfo((String) cp.rc(name).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantModule that = (ConstantModule) o;
        return name == that.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
