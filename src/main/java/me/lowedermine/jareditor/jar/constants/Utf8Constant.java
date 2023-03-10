package me.lowedermine.jareditor.jar.constants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Utf8Constant implements IConstant {
    public String value;

    public Utf8Constant() {

    }

    public Utf8Constant(DataInputStream in) throws IOException {
        int size = in.readUnsignedShort();
        byte[] bytes = new byte[size];
        int rd = in.read(bytes);
        if (rd != size) throw new IllegalArgumentException("Not enough bytes to read string");
        value = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(1);
        out.writeShort(value.length());
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        out.write(bytes);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utf8Constant that = (Utf8Constant) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
