package me.lowedermine.jareditor.jar.annotations;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TypeAnnotation extends Annotation {
    public int targetType;
    public Target target;
    public TypePath path;

    public TypeAnnotation(DataInputStream in, ConstantPool cp) throws IOException {
        targetType = in.readUnsignedByte();
        switch (targetType) {
            case 0x00:
            case 0x01:
                target = new Target.TypeParameter(in);
                break;
            case 0x10:
                target = new Target.SuperType(in);
                break;
            case 0x11:
            case 0x12:
                target = new Target.TypeParameterBound(in);
                break;
            case 0x13:
            case 0x14:
            case 0x15:
                target = new Target.Empty();
                break;
            case 0x16:
                target = new Target.FormalParameter(in);
                break;
            case 0x17:
                target = new Target.Throws(in);
                break;
            case 0x40:
            case 0x41:
                target = new Target.LocalVariable(in);
                break;
            case 0x42:
                target = new Target.Catch(in);
                break;
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x46:
                target = new Target.Offset(in);
                break;
            case 0x47:
            case 0x48:
            case 0x49:
            case 0x4A:
            case 0x4B:
                target = new Target.TypeArgument(in);
                break;
        }
        path = new TypePath(in);
        type = new FieldDescriptor((String) cp.ro(in.readUnsignedShort()));
        pairs = new ElementValuePair[in.readUnsignedShort()];
        for (int i = 0; i < pairs.length; i++) pairs[i] = new ElementValuePair(in, cp);
    }

    public void toPool(ConstantPoolBuilder cp) {
        cp.add(type.toRaw());
        for (ElementValuePair pair : pairs) pair.toPool(cp);
    }

    public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
        out.writeByte(targetType);
        target.toStream(out);
        path.toStream(out);
        out.writeShort(cp.indexOf(type.toRaw()));
        out.writeShort(pairs.length);
        for (ElementValuePair pair : pairs) pair.toStream(out, cp);
    }

    public int toLength() {
        int length = 5;
        length += target.toLength();
        length += path.toLength();
        for (ElementValuePair pair : pairs) length += pair.toLength();
        return length;
    }

    public abstract static class Target {

        public abstract void toStream(DataOutputStream out) throws IOException;

        public abstract int toLength();

        public static class TypeParameter extends Target {
            int index;

            public TypeParameter(DataInputStream in) throws IOException {
                index = in.readUnsignedByte();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeByte(index);
            }

            @Override
            public int toLength() {
                return 1;
            }
        }

        public static class SuperType extends Target {
            int index;

            public SuperType(DataInputStream in) throws IOException {
                index = in.readUnsignedShort();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeShort(index);
            }

            @Override
            public int toLength() {
                return 2;
            }
        }

        public static class TypeParameterBound extends Target {
            int index;
            int boundIndex;

            public TypeParameterBound(DataInputStream in) throws IOException {
                index = in.readUnsignedByte();
                boundIndex = in.readUnsignedByte();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeByte(index);
                out.writeByte(boundIndex);
            }

            @Override
            public int toLength() {
                return 2;
            }
        }

        public static class Empty extends Target {

            @Override
            public void toStream(DataOutputStream out) {
            }

            @Override
            public int toLength() {
                return 0;
            }
        }

        public static class FormalParameter extends Target {
            int index;

            public FormalParameter(DataInputStream in) throws IOException {
                index = in.readUnsignedByte();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeByte(index);
            }

            @Override
            public int toLength() {
                return 1;
            }
        }

        public static class Throws extends Target {
            int index;

            public Throws(DataInputStream in) throws IOException {
                index = in.readUnsignedShort();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeShort(index);
            }

            @Override
            public int toLength() {
                return 2;
            }
        }

        public static class LocalVariable extends Target {
            LocalVariable.Variable[] table;

            public LocalVariable(DataInputStream in) throws IOException {
                int tableLength = in.readUnsignedShort();
                table = new LocalVariable.Variable[tableLength];
                for (int i = 0; i < tableLength; i++) {
                    table[i] = new LocalVariable.Variable(in);
                }
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeShort(table.length);
                for (Variable variable : table) variable.toStream(out);
            }

            @Override
            public int toLength() {
                return 2 + (table.length * 6);
            }

            public static class Variable {
                int start;
                int length;
                int index;

                public Variable(DataInputStream in) throws IOException {
                    start = in.readUnsignedShort();
                    length = in.readUnsignedShort();
                    index = in.readUnsignedShort();
                }

                public void toStream(DataOutputStream out) throws IOException {
                    out.writeShort(start);
                    out.writeShort(length);
                    out.writeShort(index);
                }
            }
        }

        public static class Catch extends Target {
            int index;

            public Catch(DataInputStream in) throws IOException {
                index = in.readUnsignedShort();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeShort(index);
            }

            @Override
            public int toLength() {
                return 2;
            }
        }

        public static class Offset extends Target {
            int offset;

            public Offset(DataInputStream in) throws IOException {
                offset = in.readUnsignedShort();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeShort(offset);
            }

            @Override
            public int toLength() {
                return 2;
            }
        }

        public static class TypeArgument extends Target {
            int offset;
            int index;

            public TypeArgument(DataInputStream in) throws IOException {
                offset = in.readUnsignedShort();
                index = in.readUnsignedByte();
            }

            @Override
            public void toStream(DataOutputStream out) throws IOException {
                out.writeShort(offset);
                out.writeByte(index);
            }

            @Override
            public int toLength() {
                return 3;
            }
        }
    }

    public static class TypePath {
        public Path[] paths;

        public TypePath(DataInputStream in) throws IOException {
            int pathsLength = in.readUnsignedByte();
            paths = new Path[pathsLength];
            for (byte i = 0; i < pathsLength; i++) {
                paths[i] = new Path(in);
            }
        }

        public void toStream(DataOutputStream out) throws IOException {
            out.writeByte(paths.length);
            for (Path path : paths) path.toStream(out);
        }

        public int toLength() {
            return 2 + (paths.length * 2);
        }

        public static class Path {
            public int kind;
            public int index;

            public Path(DataInputStream in) throws IOException {
                kind = in.readUnsignedByte();
                index = in.readUnsignedByte();
            }

            public void toStream(DataOutputStream out) throws IOException {
                out.writeByte(kind);
                out.writeByte(index);
            }
        }
    }
}
