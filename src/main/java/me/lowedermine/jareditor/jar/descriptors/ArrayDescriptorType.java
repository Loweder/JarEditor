package me.lowedermine.jareditor.jar.descriptors;

import java.util.Collections;
import java.util.Objects;

public class ArrayDescriptorType implements IDescriptorFieldPart {
    public IDescriptorFieldPart arrayType;
    public int dimensions;

    public ArrayDescriptorType(String string) {
        dimensions = 1;
        int pos;
        for (pos = 0; pos < string.length(); pos++) {
            if (string.charAt(pos) == '[') dimensions++;
            else break;
        }
        arrayType = IDescriptorFieldPart.parse(string.substring(pos));
    }

    @Override
    public String toRaw() {
        return String.join("", Collections.nCopies(dimensions, "[")) + arrayType.toRaw();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayDescriptorType that = (ArrayDescriptorType) o;
        return dimensions == that.dimensions && Objects.equals(arrayType, that.arrayType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrayType, dimensions);
    }
}
