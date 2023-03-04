package me.lowedermine.jareditor.jar.descriptors;

import java.util.Objects;

public class DescriptorField implements IDescriptor {
    public IDescriptorFieldPart type;

    public DescriptorField(String string) {
        type = IDescriptorFieldPart.parse(string);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DescriptorField that = (DescriptorField) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toRaw() {
        return type.toRaw();
    }

    @Override
    public DescriptorField clone() {
        try {
            DescriptorField clone = (DescriptorField) super.clone();
            clone.type = IDescriptorFieldPart.parse(type.toRaw());
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
