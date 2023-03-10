package me.lowedermine.jareditor.jar.signatures;

public class WildcartTypeArgumenySignatureType implements ISignatureTypeArgumentsPart {
    @Override
    public String toRaw() {
        return "*";
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
