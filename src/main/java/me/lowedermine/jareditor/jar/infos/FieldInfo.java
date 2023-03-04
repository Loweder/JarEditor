package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.DescriptorField;

import java.util.Objects;

public class FieldInfo extends MemberInfo {

    public FieldInfo(String inName, DescriptorField inDesc) {
        super(inName, inDesc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldInfo fieldInfo = (FieldInfo) o;
        return Objects.equals(name, fieldInfo.name) && Objects.equals(desc, fieldInfo.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, desc);
    }
}
