package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;

import java.util.Objects;

public class MethodInfo extends MemberInfo {

    public MethodInfo(String inName, DescriptorMethod inDesc) {
        super(inName, inDesc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(desc, that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, desc);
    }
}
