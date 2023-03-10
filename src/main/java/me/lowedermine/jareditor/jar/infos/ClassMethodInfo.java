package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;

import java.util.Objects;

public class ClassMethodInfo extends ClassMemberInfo {
    public boolean interfaceMethod;

    public ClassMethodInfo(ClassInfo inClass, MemberInfo inInfo, boolean inMeth) {
        super(inClass, inInfo);
        interfaceMethod = inMeth;
    }

    @Override
    public MethodInfo getInfo() {
        return new MethodInfo(name, (MethodDescriptor) desc);
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
