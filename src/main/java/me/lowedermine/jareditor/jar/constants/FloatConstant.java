package me.lowedermine.jareditor.jar.constants;

import java.io.*;
import java.util.Objects;

public class FloatConstant implements IConstant, ILoadable {
    public float value;

    public FloatConstant() {

    }

    public FloatConstant(DataInputStream in) throws IOException {
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
        FloatConstant that = (FloatConstant) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
