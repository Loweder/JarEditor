package me.lowedermine.jareditor.jar.annotations;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Annotation {
    public FieldDescriptor type;
    public ElementValuePair[] pairs;

    protected Annotation() {

    }

    public Annotation(DataInputStream in, ConstantPool cp) throws IOException {
        type = new FieldDescriptor((String) cp.ro(in.readUnsignedShort()));
        pairs = new ElementValuePair[in.readUnsignedShort()];
        for(int i = 0; i < pairs.length; i++) pairs[i] = new ElementValuePair(in, cp);
    }

    public void toPool(ConstantPoolBuilder cp) {
        cp.add(type.toRaw());
        for (ElementValuePair pair : pairs) pair.toPool(cp);
    }

    public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
        out.writeShort(cp.indexOf(type.toRaw()));
        out.writeShort(pairs.length);
        for (ElementValuePair pair : pairs) pair.toStream(out, cp);
    }

    public int toLength() {
        int length = 4;
        for (ElementValuePair pair : pairs) length += pair.toLength();
        return length;
    }
}
