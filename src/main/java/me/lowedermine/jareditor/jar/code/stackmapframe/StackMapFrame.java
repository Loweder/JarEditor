package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.infos.ClassInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class StackMapFrame {
    public int offset;

    public static StackMapFrame parse(DataInputStream in, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        int type = in.readUnsignedByte();
        if (type < 64) {
            return new StackMapFrameSame(type, prevOffset, indexMap);
        } else if (type < 128) {
            return new StackMapFrameSameLocals(type, in, cp, prevOffset, indexMap);
        } else if (type == 247) {
            return new StackMapFrameSameLocalsExtended(in, cp, prevOffset, indexMap);
        } else if (type > 247 && type < 251) {
            return new StackMapFrameChop(type, in, prevOffset, indexMap);
        } else if (type == 251) {
            return new StackMapFrameSameExtended(in, prevOffset, indexMap);
        } else if (type > 251 && type < 255) {
            return new StackMapFrameAppend(type, in, cp, prevOffset, indexMap);
        } else if (type == 255) {
            return new StackMapFrameFull(in, cp, prevOffset, indexMap);
        }
        return null;
    }

    public void toPool(ConstantPoolBuilder cp) {
    }

    public abstract int toLength();

    public abstract void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException;

    public static abstract class VerificationInfo {
        public static VerificationInfo parse(DataInputStream in, ConstantPool cp, int[] indexMap) throws IOException {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 0:
                    return new Top();
                case 1:
                    return new Integer();
                case 2:
                    return new Float();
                case 3:
                    return new Double();
                case 4:
                    return new Long();
                case 5:
                    return new Null();
                case 6:
                    return new UninitThis();
                case 7:
                    return new Object(in, cp);
                case 8:
                    return new Uninit(in, indexMap);
                default:
                    return null;
            }
        }

        public void toPool(ConstantPoolBuilder cp) {
        }

        public int toLength() {
            return 1;
        }

        public abstract void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException;

        public static class Top extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(0);
            }
        }

        public static class Integer extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(1);
            }
        }

        public static class Float extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(2);
            }
        }

        public static class Long extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(3);
            }
        }

        public static class Double extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(4);
            }
        }

        public static class Null extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(5);
            }
        }

        public static class UninitThis extends VerificationInfo {
            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(6);
            }
        }

        public static class Object extends VerificationInfo {
            public ClassInfo info;

            public Object(DataInputStream in, ConstantPool cp) throws IOException {
                info = (ClassInfo) cp.ro(in.readUnsignedShort());
            }

            @Override
            public void toPool(ConstantPoolBuilder cp) {
                cp.add(info);
            }

            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(7);
                out.writeShort(cp.indexOf(info));
            }

            @Override
            public int toLength() {
                return 3;
            }
        }

        public static class Uninit extends VerificationInfo {
            public int offset;

            public Uninit(DataInputStream in, int[] indexMap) throws IOException {
                offset = indexMap[in.readUnsignedShort()];
            }

            @Override
            public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
                out.writeByte(8);
                out.writeShort(indexMap[offset]);
            }

            @Override
            public int toLength() {
                return 3;
            }
        }
    }
}
