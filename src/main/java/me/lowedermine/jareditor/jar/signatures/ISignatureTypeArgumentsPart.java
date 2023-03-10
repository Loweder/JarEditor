package me.lowedermine.jareditor.jar.signatures;

public interface ISignatureTypeArgumentsPart {
    static int parseLength(String string) {
        switch (string.charAt(0)) {
            case '*':
                return 1;
            case '+':
            case '-':
                return 1 + ISignatureReferencePart.parseLength(string.substring(1));
            default:
                return ISignatureReferencePart.parseLength(string);
        }
    }

    static ISignatureTypeArgumentsPart parse(String string) {
        if (string.charAt(0) == '*') {
            return new WildcartTypeArgumenySignatureType();
        }
        return new TypeArgumentSignatureType(string);
    }

    String toRaw();
}
