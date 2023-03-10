package me.lowedermine.jareditor.jar.descriptors;

import me.lowedermine.jareditor.exceptions.DescriptorException;

public interface IDescriptorFieldPart extends IDescriptorReturnPart {
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
            case 'L':
                return 1 + string.indexOf(';');
            case '[':
                return 1 + parseLength(string.substring(1));
        }
        throw new DescriptorException("Invalid descriptor type: " + string.charAt(0));
    }

    static IDescriptorFieldPart parse(String string) {
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
            case 'L':
                return new ObjectDescriptorType(string.substring(1, string.indexOf(';')));
            case '[':
                return new ArrayDescriptorType(string.substring(1));
        }
        throw new DescriptorException("Invalid descriptor type: " + string.charAt(0));
    }
}
