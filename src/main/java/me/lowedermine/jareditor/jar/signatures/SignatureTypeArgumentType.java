package me.lowedermine.jareditor.jar.signatures;

import java.util.Objects;

public class SignatureTypeArgumentType implements ISignatureTypeArgumentsPart {
    public SignatureWildcardIndicatorType indicator;
    public ISignatureReferencePart value;

    public SignatureTypeArgumentType(String string) {
        switch (string.charAt(0)) {
            case '+':
                indicator = SignatureWildcardIndicatorType.PLUS;
                value = ISignatureReferencePart.parse(string.substring(1));
                break;
            case '-':
                indicator = SignatureWildcardIndicatorType.MINUS;
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
        SignatureTypeArgumentType that = (SignatureTypeArgumentType) o;
        return indicator == that.indicator && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indicator, value);
    }
}
