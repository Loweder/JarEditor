package me.lowedermine.jareditor.jar.signatures;

import java.util.Collections;
import java.util.Objects;

public class ArraySignatureType implements ISignatureReferencePart {
    public ISignatureJavaTypePart arrayType;
    public int dimensions;

    public ArraySignatureType(String string) {
        dimensions = 1;
        int pos;
        for (pos = 0; pos < string.length(); pos++) {
            if (string.charAt(pos) == '[') dimensions++;
            else break;
        }
        arrayType = ISignatureJavaTypePart.parse(string.substring(pos));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArraySignatureType that = (ArraySignatureType) o;
        return dimensions == that.dimensions && Objects.equals(arrayType, that.arrayType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrayType, dimensions);
    }

    @Override
    public String toRaw() {
        return String.join("", Collections.nCopies(dimensions, "[")) + arrayType.toRaw();
    }
}
