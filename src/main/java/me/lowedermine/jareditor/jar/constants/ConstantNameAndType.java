package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.descriptors.DescriptorField;
import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;
import me.lowedermine.jareditor.jar.descriptors.IDescriptor;
import me.lowedermine.jareditor.jar.infos.FieldInfo;
import me.lowedermine.jareditor.jar.infos.MethodInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ConstantNameAndType implements IConstant {
    public int name;
    public int type;

    public ConstantNameAndType() {

    }

    public ConstantNameAndType(DataInputStream in) throws IOException {
        name = in.readUnsignedShort();
        type = in.readUnsignedShort();
    }

    @Override
    public void toBytes(DataOutputStream out) throws IOException {
        out.writeByte(12);
        out.writeShort(name);
        out.writeShort(type);
    }

    @Override
    public Object objectify(ConstantPool cp) {
        IDescriptor desc = IDescriptor.parse((String) cp.rc(type).objectify(cp));
        if (desc instanceof DescriptorMethod)
            return new MethodInfo((String) cp.rc(name).objectify(cp), (DescriptorMethod) desc);
        else
            return new FieldInfo((String) cp.rc(name).objectify(cp), (DescriptorField) desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantNameAndType that = (ConstantNameAndType) o;
        return name == that.name && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
