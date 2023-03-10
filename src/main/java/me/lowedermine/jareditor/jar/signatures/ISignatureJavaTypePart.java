package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.jar.descriptors.PrimitiveDescriptorType;

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
                return PrimitiveDescriptorType.BYTE;
            case 'C':
                return PrimitiveDescriptorType.CHAR;
            case 'D':
                return PrimitiveDescriptorType.DOUBLE;
            case 'F':
                return PrimitiveDescriptorType.FLOAT;
            case 'I':
                return PrimitiveDescriptorType.INT;
            case 'J':
                return PrimitiveDescriptorType.LONG;
            case 'S':
                return PrimitiveDescriptorType.SHORT;
            case 'Z':
                return PrimitiveDescriptorType.BOOLEAN;
            default:
                return ISignatureReferencePart.parse(string);
        }
    }
}
