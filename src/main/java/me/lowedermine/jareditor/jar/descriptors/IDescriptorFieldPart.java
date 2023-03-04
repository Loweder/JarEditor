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
                return DescriptorBaseType.BYTE;
            case 'C':
                return DescriptorBaseType.CHAR;
            case 'D':
                return DescriptorBaseType.DOUBLE;
            case 'F':
                return DescriptorBaseType.FLOAT;
            case 'I':
                return DescriptorBaseType.INT;
            case 'J':
                return DescriptorBaseType.LONG;
            case 'S':
                return DescriptorBaseType.SHORT;
            case 'Z':
                return DescriptorBaseType.BOOLEAN;
            case 'L':
                return new DescriptorObjectType(string.substring(1, string.indexOf(';')));
            case '[':
                return new DescriptorArrayType(string.substring(1));
        }
        throw new DescriptorException("Invalid descriptor type: " + string.charAt(0));
    }
}
