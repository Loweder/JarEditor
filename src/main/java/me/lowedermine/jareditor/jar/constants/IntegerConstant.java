package me.lowedermine.jareditor.jar.constants;

import java.io.*;
import java.util.Objects;

public class IntegerConstant implements IConstant, ILoadable {
    public int value;

    public IntegerConstant() {

    }

    public IntegerConstant(DataInputStream in) throws IOException {
        value = in.readInt();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(3);
        out.writeInt(value);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerConstant that = (IntegerConstant) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
