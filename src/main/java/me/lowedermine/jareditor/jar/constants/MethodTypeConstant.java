package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;

import java.io.*;
import java.util.Objects;

public class MethodTypeConstant implements IConstant, ILoadable {
    public int descriptor;

    public MethodTypeConstant() {

    }

    public MethodTypeConstant(DataInputStream in) throws IOException {
        descriptor = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(16);
        out.writeShort(descriptor);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new MethodDescriptor((String) cp.rc(descriptor).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodTypeConstant that = (MethodTypeConstant) o;
        return descriptor == that.descriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }
}
