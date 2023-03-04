package me.lowedermine.jareditor.jar.constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IConstant {
    static IConstant read(DataInputStream in) throws IOException {
        int tag = in.readByte();
        switch (tag) {
            case 1:
                return new ConstantUtf8(in);
            case 3:
                return new ConstantInteger(in);
            case 4:
                return new ConstantFloat(in);
            case 5:
                return new ConstantLong(in);
            case 6:
                return new ConstantDouble(in);
            case 7:
                return new ConstantClass(in);
            case 8:
                return new ConstantString(in);
            case 9:
                return new ConstantFieldRef(in);
            case 10:
                return new ConstantMethodRef(in);
            case 11:
                return new ConstantIMethodRef(in);
            case 12:
                return new ConstantNameAndType(in);
            case 15:
                return new ConstantMethodHandle(in);
            case 16:
                return new ConstantMethodType(in);
            case 17:
                return new ConstantDynamic(in);
            case 18:
                return new ConstantInvokeDynamic(in);
            case 19:
                return new ConstantModule(in);
            case 20:
                return new ConstantPackage(in);
            default:
                throw new IOException("Class resolving error: unknown constant type " + tag);
        }
    }

    void toBytes(DataOutputStream out) throws IOException;

    Object objectify(ConstantPool cp);

    boolean equals(Object other);
}
