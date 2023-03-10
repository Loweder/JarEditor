package me.lowedermine.jareditor.jar.signatures;

import java.util.Objects;

public class TypeArgumentSignatureType implements ISignatureTypeArgumentsPart {
    public WildcartIndicatorSignatureType indicator;
    public ISignatureReferencePart value;

    public TypeArgumentSignatureType(String string) {
        switch (string.charAt(0)) {
            case '+':
                indicator = WildcartIndicatorSignatureType.PLUS;
                value = ISignatureReferencePart.parse(string.substring(1));
                break;
            case '-':
                indicator = WildcartIndicatorSignatureType.MINUS;
                value = ISignatureReferencePart.parse(string.substring(1));
                break;
            default:
                indicator = null;
                value = ISignatureReferencePart.parse(string);
                break;
        }
    }

    @Override
    public String toRaw() {
        String indicatorString = indicator == null ? "" : indicator.toRaw();
        String valueString = value.toRaw();
        return indicatorString + valueString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeArgumentSignatureType that = (TypeArgumentSignatureType) o;
        return indicator == that.indicator && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indicator, value);
    }
}
