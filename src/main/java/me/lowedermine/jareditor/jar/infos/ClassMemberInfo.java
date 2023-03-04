package me.lowedermine.jareditor.jar.infos;

import java.util.Objects;

public abstract class ClassMemberInfo extends MemberInfo{
    public ClassInfo clazz;

    protected ClassMemberInfo() {

    }

    public ClassMemberInfo(ClassInfo inClass, MemberInfo inInfo) {
        super(inInfo != null ? inInfo.name : null, inInfo != null ? inInfo.desc : null);
        clazz = inClass;
    }

    public abstract MemberInfo getInfo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClassMemberInfo that = (ClassMemberInfo) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), clazz);
    }

    @Override
    public ClassMemberInfo clone() {
        ClassMemberInfo clone = (ClassMemberInfo) super.clone();
        clone.clazz = clazz.clone();
        return clone;
    }
}
