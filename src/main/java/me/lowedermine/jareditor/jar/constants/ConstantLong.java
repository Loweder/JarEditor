package me.lowedermine.jareditor.jar.constants;

import java.io.*;
import java.util.Objects;

public class ConstantLong implements IConstant, ILoadable {
    public long value;

    public ConstantLong() {

    }

    public ConstantLong(DataInputStream in) throws IOException {
        value = in.readLong();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(5);
        out.writeLong(value);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantLong that = (ConstantLong) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
