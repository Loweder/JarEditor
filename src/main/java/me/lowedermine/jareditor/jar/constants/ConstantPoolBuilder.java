package me.lowedermine.jareditor.jar.constants;

import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;
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
            ClassConstant constant = new ClassConstant();
            constant.name = add(((ClassInfo) o).toRaw());
            return add0(constant);
        } else if (o instanceof Integer) {
            IntegerConstant constant = new IntegerConstant();
            constant.value = (int) o;
            return add0(constant);
        } else if (o instanceof Long) {
            LongConstant constant = new LongConstant();
            constant.value = (long) o;
            return add0(constant);
        } else if (o instanceof Float) {
            FloatConstant constant = new FloatConstant();
            constant.value = (float) o;
            return add0(constant);
        } else if (o instanceof Double) {
            DoubleConstant constant = new DoubleConstant();
            constant.value = (double) o;
            return add0(constant);
        } else if (o instanceof BootstrapFieldInfo) {
            DynamicConstant constant = new DynamicConstant();
            constant.methodAttr = ((BootstrapFieldInfo) o).index;
            constant.nameAndType = add(((BootstrapFieldInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof BootstrapMethodInfo) {
            InvokeDynamicConstant constant = new InvokeDynamicConstant();
            constant.methodAttr = ((BootstrapMethodInfo) o).index;
            constant.nameAndType = add(((BootstrapMethodInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof ClassFieldInfo) {
            FieldRefConstant constant = new FieldRefConstant();
            constant.clazz = add(((ClassFieldInfo) o).clazz);
            constant.nameAndType = add(((ClassFieldInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof ClassMethodInfo) {
            if (((ClassMethodInfo) o).interfaceMethod) {
                IMethodRefConstant constant = new IMethodRefConstant();
                constant.clazz = add(((ClassMethodInfo) o).clazz);
                constant.nameAndType = add(((ClassMethodInfo) o).getInfo());
                return add0(constant);
            }
            MethodRefConstant constant = new MethodRefConstant();
            constant.clazz = add(((ClassMethodInfo) o).clazz);
            constant.nameAndType = add(((ClassMethodInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof FieldInfo) {
            NameAndTypeConstant constant = new NameAndTypeConstant();
            constant.name = add(((FieldInfo) o).name);
            constant.type = add(((FieldInfo) o).desc.toRaw());
            return add0(constant);
        } else if (o instanceof MethodInfo) {
            NameAndTypeConstant constant = new NameAndTypeConstant();
            constant.name = add(((MethodInfo) o).name);
            constant.type = add(((MethodInfo) o).desc.toRaw());
            return add0(constant);
        } else if (o instanceof MethodHandleInfo) {
            MethodHandleConstant constant = new MethodHandleConstant();
            constant.referenceKind = ((MethodHandleInfo) o).type.to();
            constant.referenceValue = add(((MethodHandleInfo) o).getInfo());
            return add0(constant);
        } else if (o instanceof MethodDescriptor) {
            MethodTypeConstant constant = new MethodTypeConstant();
            constant.descriptor = add(((MethodDescriptor) o).toRaw());
            return add0(constant);
        } else if (o instanceof ModuleInfo) {
            ModuleConstant constant = new ModuleConstant();
            constant.name = add(((ModuleInfo) o).toRaw());
            return add0(constant);
        } else if (o instanceof PackageInfo) {
            PackageConstant constant = new PackageConstant();
            constant.name = add(((PackageInfo) o).toRaw());
            return add0(constant);
        } else if (o instanceof StringInfo) {
            StringConstant constant = new StringConstant();
            constant.string = add(((StringInfo) o).value);
            return add0(constant);
        } else if (o instanceof String) {
            Utf8Constant constant = new Utf8Constant();
            constant.value = (String) o;
            return add0(constant);
        } else if (o == null) {
            NullConstant constant = new NullConstant();
            return add0(constant);
        }
        return 0;
    }

    private int add0(IConstant constant) {
        if (!raw.contains(constant)) {
            raw.add(constant);
            if (constant instanceof DoubleConstant || constant instanceof LongConstant)
                raw.add(new NullConstant());
        }
        return raw.indexOf(constant);
    }

    public ConstantPool build() {
        return new ConstantPool(this.raw.toArray(new IConstant[0]));
    }
}
