package me.lowedermine.jareditor.jar.constants;

import java.io.*;
import java.util.Objects;

public class ConstantDouble implements IConstant, ILoadable {
    public double value;

    public ConstantDouble() {

    }

    public ConstantDouble(DataInputStream in) throws IOException {
        value = in.readDouble();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(6);
        out.writeDouble(value);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantDouble that = (ConstantDouble) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
