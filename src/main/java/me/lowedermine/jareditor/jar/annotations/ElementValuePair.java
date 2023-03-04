package me.lowedermine.jareditor.jar.annotations;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.descriptors.DescriptorField;
import me.lowedermine.jareditor.jar.infos.FieldInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ElementValuePair {

    public String name;
    public ElementValue value;

    public ElementValuePair(DataInputStream in, ConstantPool cp) throws IOException {
        name = (String) cp.ro(in.readUnsignedShort());
        value = new ElementValue(in, cp);
    }

    public void toPool(ConstantPoolBuilder cp) {
        cp.add(name);
        value.toPool(cp);
    }

    public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
        out.writeShort(cp.indexOf(name));
        value.toStream(out, cp);
    }

    public int toLength() {
        return 2 + value.toLength();
    }

    public static class ElementValue {

        public Type type;
        public Object value = null;

        public ElementValue(DataInputStream in, ConstantPool cp) throws IOException{
            type = Type.fromCode(in.readUnsignedByte());
            switch (Objects.requireNonNull(type)) {
                case BYTE:
                case STRING:
                case BOOLEAN:
                case SHORT:
                case LONG:
                case INT:
                case FLOAT:
                case DOUBLE:
                case CHAR:
                case CLASS:
                    value = cp.ro(in.readUnsignedShort());
                    break;
                case ENUM:
                    String desc = (String) cp.ro(in.readUnsignedShort());
                    String name = (String) cp.ro(in.readUnsignedShort());
                    value = new FieldInfo(name, new DescriptorField(desc));
                    break;
                case ANNOTATION:
                    value = new Annotation(in, cp);
                    break;
                case ARRAY:
                    int size = in.readUnsignedShort();
                    value = new ElementValue[size];
                    for (int i = 0; i < size; i++) ((ElementValue[])value)[i] = new ElementValue(in, cp);
                    break;
            }
        }

        public void toPool(ConstantPoolBuilder cp) {
            switch (Objects.requireNonNull(type)) {
                case BYTE:
                case STRING:
                case BOOLEAN:
                case SHORT:
                case LONG:
                case INT:
                case FLOAT:
                case DOUBLE:
                case CHAR:
                case CLASS:
                    cp.add(value);
                    break;
                case ENUM:
                    FieldInfo fValue = (FieldInfo) value;
                    cp.add(fValue.name);
                    cp.add(fValue.desc.toRaw());
                    break;
                case ANNOTATION:
                    ((Annotation) value).toPool(cp);
                    break;
                case ARRAY:
                    for (ElementValue elementValue : ((ElementValue[]) value)) elementValue.toPool(cp);
                    break;
            }
        }

        public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
            out.writeByte(type.type);
            switch (Objects.requireNonNull(type)) {
                case BYTE:
                case STRING:
                case BOOLEAN:
                case SHORT:
                case LONG:
                case INT:
                case FLOAT:
                case DOUBLE:
                case CHAR:
                case CLASS:
                    out.writeShort(cp.indexOf(value));
                    break;
                case ENUM:
                    FieldInfo fValue = (FieldInfo) value;
                    out.writeShort(cp.indexOf(fValue.name));
                    out.writeShort(cp.indexOf(fValue.desc.toRaw()));
                    break;
                case ANNOTATION:
                    ((Annotation) value).toStream(out, cp);
                    break;
                case ARRAY:
                    out.writeShort(((ElementValue[]) value).length);
                    for (ElementValue elementValue : ((ElementValue[]) value)) elementValue.toStream(out, cp);
                    break;
            }
        }

        public int toLength() {
            switch (Objects.requireNonNull(type)) {
                case BYTE:
                case STRING:
                case BOOLEAN:
                case SHORT:
                case LONG:
                case INT:
                case FLOAT:
                case DOUBLE:
                case CHAR:
                case CLASS:
                    return 3;
                case ENUM:
                    return 5;
                case ANNOTATION:
                    return ((Annotation) value).toLength();
                case ARRAY:
                    int length = 3;
                    for (ElementValue elementValue : ((ElementValue[]) value)) length += elementValue.toLength();
                    return length;
            }
            return 0;
        }

        public enum Type {
            BYTE('B'), CHAR('C'), DOUBLE('D'), FLOAT('F'), INT('I'), LONG('J'), SHORT('S'), BOOLEAN('Z'), STRING('s'), ENUM('e'), CLASS('c'), ANNOTATION('@'), ARRAY('[');

            private final char type;

            Type(char type) {
                this.type = type;
            }

            public static Type fromCode(int code) {
                for (Type value : values()) {
                    if (value.type == code) return value;
                }
                return null;
            }
        }
    }
}
