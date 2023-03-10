package me.lowedermine.jareditor.jar.constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ConstantPool {
    private final IConstant[] pool;
    private final Object[] builtPool;

    public ConstantPool(IConstant[] pool) {
        this.pool = pool;
        builtPool = new Object[pool.length];
        for (int i = 0; i < pool.length; i++) builtPool[i] = pool[i].objectify(this);
    }

    public ConstantPool(DataInputStream in) throws IOException {
        pool = new IConstant[in.readUnsignedShort()];
        pool[0] = new NullConstant();
        for (int i = 1; i < pool.length; i++) {
            pool[i] = IConstant.read(in);
            if (pool[i] instanceof DoubleConstant || pool[i] instanceof LongConstant) {
                pool[++i] = new NullConstant();
            }
        }
        builtPool = new Object[pool.length];
        for (int i = 0; i < pool.length; i++) builtPool[i] = pool[i].objectify(this);
    }

    public void toBytes(DataOutputStream in) throws IOException {
        in.writeShort(pool.length);
        for (IConstant constant : pool) constant.toBytes(in);
    }

    public IConstant rc(int index) {
        return pool[index];
    }

    public Object ro(int index) {
        return pool[index].objectify(this);
    }

    public int indexOf(Object o) {
        if (o instanceof IConstant) {
            for (int i = 0; i < pool.length; i++) if (pool[i].equals(o)) return i;
        } else {
            for (int i = 0; i < builtPool.length; i++) if (Objects.equals(builtPool[i], o)) return i;
        }
        return -1;
    }
}
