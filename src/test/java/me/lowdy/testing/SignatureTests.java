package me.lowdy.testing;

import me.lowdy.testing.data.DummyClass;
import me.lowdy.testing.data.DummyInterface;
import me.lowdy.testing.data.GenericSuperclass;
import me.lowdy.testing.data.GenericSuperinterface;

public class SignatureTests<T, K extends Exception, Z extends String> extends GenericSuperclass<T> implements GenericSuperinterface<K> {
    public GenericSuperclass<? extends K> field;

    public <X extends DummyClass & DummyInterface, Y extends Throwable> GenericSuperinterface<?> method(GenericSuperclass<X>.GenericSubclass<Y> test, K test2) throws K, Y {
        return null;
    }

    public static void trigger() {

    }
}
