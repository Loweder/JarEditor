package me.lowedermine.jareditor.jar.constants;

import java.io.*;
import java.util.Objects;

public class ConstantFloat implements IConstant, ILoadable {
    public float value;

    public ConstantFloat() {

    }

    public ConstantFloat(DataInputStream in) throws IOException {
        value = in.readFloat();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(4);
        out.writeFloat(value);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantFloat that = (ConstantFloat) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
