package me.lowedermine.jareditor.jar.descriptors;

import me.lowedermine.jareditor.utils.IMyCloneable;

public interface IDescriptor extends IMyCloneable {

    static IDescriptor parse(String string) {
        if (string.charAt(0) == '(') {
            return new MethodDescriptor(string);
        } else {
            return new FieldDescriptor(string);
        }
    }

    String toRaw();

    IDescriptor clone();
}
