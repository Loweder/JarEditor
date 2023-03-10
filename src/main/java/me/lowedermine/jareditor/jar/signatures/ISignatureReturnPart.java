package me.lowedermine.jareditor.jar.signatures;

public interface ISignatureReturnPart {
    static int parseLength(String string) {
        if (string.charAt(0) == 'V') {
            return 1;
        }
        return ISignatureJavaTypePart.parseLength(string);
    }

    static ISignatureReturnPart parse(String string) {
        if (string.charAt(0) == 'V') {
            return new VoidSignatureType();
        }
        return ISignatureJavaTypePart.parse(string);
    }

    String toRaw();
}
