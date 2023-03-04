package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;

import java.io.*;
import java.util.Objects;

public class ConstantMethodType implements IConstant, ILoadable {
    public int descriptor;

    public ConstantMethodType() {

    }

    public ConstantMethodType(DataInputStream in) throws IOException {
        descriptor = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(16);
        out.writeShort(descriptor);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new DescriptorMethod((String) cp.rc(descriptor).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantMethodType that = (ConstantMethodType) o;
        return descriptor == that.descriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }
}
