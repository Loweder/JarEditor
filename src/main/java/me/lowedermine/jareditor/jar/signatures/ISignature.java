package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.utils.MyCloneable;

public interface ISignature extends MyCloneable {
    static ISignature parse(String string) {
        if (string.contains("("))
            return new SignatureMethod(string);
        else
            return new SignatureClass(string);
    }

    String toRaw();
}
