package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;

import java.util.Objects;

public class ClassMethodInfo extends ClassMemberInfo {
    public ClassInfo clazz;
    public boolean interfaceMethod;

    public ClassMethodInfo(ClassInfo inClass, MemberInfo inInfo, boolean inMeth) {
        super(inClass, inInfo);
        interfaceMethod = inMeth;
    }

    @Override
    public MemberInfo getInfo() {
        return new MethodInfo(name, (DescriptorMethod) desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClassMethodInfo that = (ClassMethodInfo) o;
        return interfaceMethod == that.interfaceMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), interfaceMethod);
    }
}
