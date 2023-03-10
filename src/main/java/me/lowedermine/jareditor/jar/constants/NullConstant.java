package me.lowedermine.jareditor.jar.constants;

import java.io.DataOutputStream;

public class NullConstant implements IConstant {
    @Override
    public void toBytes(DataOutputStream out) {}

    @Override
    public Object objectify(ConstantPool cp) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
