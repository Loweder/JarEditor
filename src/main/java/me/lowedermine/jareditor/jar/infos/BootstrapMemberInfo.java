package me.lowedermine.jareditor.jar.infos;

import java.util.Objects;

public abstract class BootstrapMemberInfo extends MemberInfo {
    public int index;

    public BootstrapMemberInfo(int index, MemberInfo inInfo) {
        super(inInfo.name, inInfo.desc);
        this.index = index;
    }

    public abstract MemberInfo getInfo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BootstrapMemberInfo that = (BootstrapMemberInfo) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), index);
    }

    @Override
    public BootstrapMemberInfo clone() {
        return (BootstrapMemberInfo) super.clone();
    }
}
