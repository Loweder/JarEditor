package me.lowedermine.jareditor.jar.descriptors;

import me.lowedermine.jareditor.utils.MyCloneable;

public interface IDescriptor extends MyCloneable {

    static IDescriptor parse(String string) {
        if (string.charAt(0) == '(') {
            return new DescriptorMethod(string);
        } else {
            return new DescriptorField(string);
        }
    }

    String toRaw();

    IDescriptor clone();
}
