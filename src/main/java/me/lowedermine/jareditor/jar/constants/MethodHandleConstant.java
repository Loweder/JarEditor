package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.ClassMemberInfo;
import me.lowedermine.jareditor.jar.infos.MethodHandleInfo;

import java.io.*;
import java.util.Objects;

public class MethodHandleConstant implements IConstant, ILoadable {
    public int referenceKind;
    public int referenceValue;

    public MethodHandleConstant() {

    }

    public MethodHandleConstant(DataInputStream in) throws IOException {
        referenceKind = in.readUnsignedByte();
        referenceValue = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(15);
        out.writeByte(referenceKind);
        out.writeShort(referenceValue);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new MethodHandleInfo(referenceKind, (ClassMemberInfo) cp.rc(referenceValue).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodHandleConstant that = (MethodHandleConstant) o;
        return referenceKind == that.referenceKind && referenceValue == that.referenceValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceKind, referenceValue);
    }
}
