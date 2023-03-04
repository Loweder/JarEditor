package me.lowedermine.jareditor.jar.descriptors;

public interface IDescriptorReturnPart {
    static int parseLength(String string) {
        if (string.charAt(0) == 'V') {
            return 1;
        }
        return IDescriptorFieldPart.parseLength(string);
    }

    static IDescriptorReturnPart parse(String string) {
        if (string.charAt(0) == 'V') {
            return new DescriptorVoidType();
        }
        return IDescriptorFieldPart.parse(string);
    }

    String toRaw();
}
