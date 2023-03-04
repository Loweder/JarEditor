package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;
import me.lowedermine.jareditor.jar.infos.*;

import java.util.ArrayList;
import java.util.List;

public class ConstantPoolBuilder {
    private final List<IConstant> raw = new ArrayList<>();

    public void addAll(Object[] o) {
        for (Object o1 : o) {
            add(o1);
        }
    }

    public int add(Object o) {
        if (o instanceof ClassInfo) {
            ConstantClass constant = new ConstantClass();
            constant.name = add(((ClassInfo) o).toRaw());
            return add0(constant);
        } else if (o instanceof Integer) {
            ConstantInteger constant = new ConstantInteger();
            constant.value = (int) o;
            return add0(constant);
        } else if (o instanceof Long) {
            ConstantLong constant = new ConstantLong();
            constant.value = (long) o;
            return add0(constant);
        } else if (o instanceof Float) {
            ConstantFloat constant = new ConstantFloat();
            constant.value = (float) o;
            return add0(constant);
        } else if (o instanceof Double) {
            ConstantDouble constant = new ConstantDouble();
            constant.value = (double) o;
            return add0(constant);
        } else if (o instanceof BootstrapFieldInfo) {
            ConstantDynamic constant = new ConstantDynamic();
            constant.methodAttr = ((BootstrapFieldInfo) o).index;
            constant.nameAndType = add(((BootstrapFieldInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof BootstrapMethodInfo) {
            ConstantInvokeDynamic constant = new ConstantInvokeDynamic();
            constant.methodAttr = ((BootstrapMethodInfo) o).index;
            constant.nameAndType = add(((BootstrapMethodInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof ClassFieldInfo) {
            ConstantFieldRef constant = new ConstantFieldRef();
            constant.clazz = add(((ClassFieldInfo) o).clazz);
            constant.nameAndType = add(((ClassFieldInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof ClassMethodInfo) {
            if (((ClassMethodInfo) o).interfaceMethod) {
                ConstantIMethodRef constant = new ConstantIMethodRef();
                constant.clazz = add(((ClassMethodInfo) o).clazz);
                constant.nameAndType = add(((ClassMethodInfo) o).getInfo());
                return add0(constant);
            }
            ConstantMethodRef constant = new ConstantMethodRef();
            constant.clazz = add(((ClassMethodInfo) o).clazz);
            constant.nameAndType = add(((ClassMethodInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof FieldInfo) {
            ConstantNameAndType constant = new ConstantNameAndType();
            constant.name = add(((FieldInfo) o).name);
            constant.type = add(((FieldInfo) o).desc.toRaw());
            return add0(constant);
        } else if (o instanceof MethodInfo) {
            ConstantNameAndType constant = new ConstantNameAndType();
            constant.name = add(((MethodInfo) o).name);
            constant.type = add(((MethodInfo) o).desc.toRaw());
            return add0(constant);
        } else if (o instanceof MethodHandleInfo) {
            ConstantMethodHandle constant = new ConstantMethodHandle();
            constant.referenceKind = ((MethodHandleInfo) o).type.to();
            constant.referenceValue = add(((MethodHandleInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof DescriptorMethod) {
            ConstantMethodType constant = new ConstantMethodType();
            constant.descriptor = add(((DescriptorMethod) o).toRaw());
            return add0(constant);
        } else if (o instanceof ModuleInfo) {
            ConstantModule constant = new ConstantModule();
            constant.name = add(((ModuleInfo) o).toRaw());
            return add0(constant);
        } else if (o instanceof PackageInfo) {
            ConstantPackage constant = new ConstantPackage();
            constant.name = add(((PackageInfo) o).toRaw());
            return add0(constant);
        } else if (o instanceof StringInfo) {
            ConstantString constant = new ConstantString();
            constant.string = add(((StringInfo) o).value);
            return add0(constant);
        } else if (o instanceof String) {
            ConstantUtf8 constant = new ConstantUtf8();
            constant.value = (String) o;
            return add0(constant);
        } else if (o == null) {
            ConstantNullPtr constant = new ConstantNullPtr();
            return add0(constant);
        }
        return 0;
    }

    private int add0(IConstant constant) {
        if (!raw.contains(constant)) raw.add(constant);
        return raw.indexOf(constant);
    }

    public ConstantPool build() {
        return new ConstantPool(this.raw.toArray(new IConstant[0]));
    }
}
