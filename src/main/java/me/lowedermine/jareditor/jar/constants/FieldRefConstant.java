package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.ClassFieldInfo;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.MemberInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FieldRefConstant implements IConstant {
    public int clazz;
    public int nameAndType;

    public FieldRefConstant() {

    }

    public FieldRefConstant(DataInputStream in) throws IOException {
        clazz = in.readUnsignedShort();
        nameAndType = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(9);
        out.writeShort(clazz);
        out.writeShort(nameAndType);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new ClassFieldInfo((ClassInfo) cp.rc(clazz).objectify(cp), (MemberInfo) cp.rc(nameAndType).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldRefConstant that = (FieldRefConstant) o;
        return clazz == that.clazz && nameAndType == that.nameAndType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, nameAndType);
    }
}
