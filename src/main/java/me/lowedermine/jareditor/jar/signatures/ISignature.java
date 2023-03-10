package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.utils.IMyCloneable;

public interface ISignature extends IMyCloneable {
    static ISignature parse(String string) {
        if (string.contains("("))
            return new MethodSignature(string);
        else
            return new ClassSignature(string);
    }

    String toRaw();
}
