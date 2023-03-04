package me.lowedermine.jareditor.jar.signatures;

public enum SignatureWildcardIndicatorType implements ISignatureJavaTypePart {
    PLUS, MINUS;

    @Override
    public String toRaw() {
        return this == PLUS ? "+" : "-";
    }
}
