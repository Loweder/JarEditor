package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.BootstrapMethodInfo;
import me.lowedermine.jareditor.jar.infos.MemberInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ConstantInvokeDynamic implements IConstant {
    public int methodAttr;
    public int nameAndType;

    public ConstantInvokeDynamic() {

    }

    public ConstantInvokeDynamic(DataInputStream in) throws IOException {
        methodAttr = in.readUnsignedShort();
        nameAndType = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(18);
        out.writeShort(methodAttr);
        out.writeShort(nameAndType);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new BootstrapMethodInfo(methodAttr, (MemberInfo) cp.rc(nameAndType).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantInvokeDynamic that = (ConstantInvokeDynamic) o;
        return methodAttr == that.methodAttr && nameAndType == that.nameAndType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodAttr, nameAndType);
    }
}
