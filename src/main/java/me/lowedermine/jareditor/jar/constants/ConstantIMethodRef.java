package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.*;

import java.io.*;
import java.util.Objects;

public class ConstantIMethodRef implements IConstant {
    public int clazz;
    public int nameAndType;

    public ConstantIMethodRef() {

    }

    public ConstantIMethodRef(DataInputStream in) throws IOException {
        clazz = in.readUnsignedShort();
        nameAndType = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(11);
        out.writeShort(clazz);
        out.writeShort(nameAndType);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new ClassMethodInfo((ClassInfo) cp.rc(clazz).objectify(cp), (MemberInfo) cp.rc(nameAndType).objectify(cp), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantIMethodRef that = (ConstantIMethodRef) o;
        return clazz == that.clazz && nameAndType == that.nameAndType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, nameAndType);
    }
}
