package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.IDescriptor;
import me.lowedermine.jareditor.utils.MyCloneable;

import java.util.Objects;

public abstract class MemberInfo implements MyCloneable {
    public String name;
    public IDescriptor desc;

    protected MemberInfo() {

    }

    public MemberInfo(String inName, IDescriptor inDesc) {
        name = inName;
        desc = inDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberInfo memberInfo = (MemberInfo) o;
        return Objects.equals(name, memberInfo.name) && Objects.equals(desc, memberInfo.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, desc);
    }

    @Override
    public MemberInfo clone() {
        try {
            MemberInfo clone = (MemberInfo) super.clone();
            clone.desc = desc.clone();
            return clone;
        } catch (CloneNotSupportedException ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
