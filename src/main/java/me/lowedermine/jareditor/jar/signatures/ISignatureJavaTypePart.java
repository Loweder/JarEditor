package me.lowedermine.jareditor.jar.signatures;

public interface ISignatureJavaTypePart extends ISignatureReturnPart {
    static int parseLength(String string) {
        switch (string.charAt(0)) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z':
                return 1;
            default:
                return ISignatureReferencePart.parseLength(string);
        }
    }

    static ISignatureJavaTypePart parse(String string) {
        switch (string.charAt(0)) {
            case 'B':
                return SignatureBaseType.BYTE;
            case 'C':
                return SignatureBaseType.CHAR;
            case 'D':
                return SignatureBaseType.DOUBLE;
            case 'F':
                return SignatureBaseType.FLOAT;
            case 'I':
                return SignatureBaseType.INT;
            case 'J':
                return SignatureBaseType.LONG;
            case 'S':
                return SignatureBaseType.SHORT;
            case 'Z':
                return SignatureBaseType.BOOLEAN;
            default:
                return ISignatureReferencePart.parse(string);
        }
    }
}
