package me.lowedermine.jareditor.jar.signatures;

public class VoidSignatureType implements ISignatureReturnPart {
    @Override
    public String toRaw() {
        return "V";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
