package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.infos.BootstrapFieldInfo;
import me.lowedermine.jareditor.jar.infos.MemberInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ConstantDynamic implements IConstant, ILoadable {
    public int methodAttr;
    public int nameAndType;

    public ConstantDynamic() {

    }

    public ConstantDynamic(DataInputStream in) throws IOException {
        methodAttr = in.readUnsignedShort();
        nameAndType = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(17);
        out.writeShort(methodAttr);
        out.writeShort(nameAndType);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        return new BootstrapFieldInfo(methodAttr, (MemberInfo) cp.rc(nameAndType).objectify(cp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantDynamic that = (ConstantDynamic) o;
        return methodAttr == that.methodAttr && nameAndType == that.nameAndType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodAttr, nameAndType);
    }
}
