package me.lowedermine.jareditor.jar.constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IConstant {
    static IConstant read(DataInputStream in) throws IOException {
        int tag = in.readByte();
        switch (tag) {
            case 1:
                return new Utf8Constant(in);
            case 3:
                return new IntegerConstant(in);
            case 4:
                return new FloatConstant(in);
            case 5:
                return new LongConstant(in);
            case 6:
                return new DoubleConstant(in);
            case 7:
                return new ClassConstant(in);
            case 8:
                return new StringConstant(in);
            case 9:
                return new FieldRefConstant(in);
            case 10:
                return new MethodRefConstant(in);
            case 11:
                return new IMethodRefConstant(in);
            case 12:
                return new NameAndTypeConstant(in);
            case 15:
                return new MethodHandleConstant(in);
            case 16:
                return new MethodTypeConstant(in);
            case 17:
                return new DynamicConstant(in);
            case 18:
                return new InvokeDynamicConstant(in);
            case 19:
                return new ModuleConstant(in);
            case 20:
                return new PackageConstant(in);
            default:
                throw new IOException("Class resolving error: unknown constant type " + tag);
        }
    }

    void toBytes(DataOutputStream out) throws IOException;

    Object objectify(ConstantPool cp);

    boolean equals(Object other);
}
