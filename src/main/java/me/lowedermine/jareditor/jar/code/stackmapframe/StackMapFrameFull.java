package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StackMapFrameFull extends StackMapFrame {
    public VerificationInfo[] locals;
    public VerificationInfo[] stack;

    public StackMapFrameFull(DataInputStream in, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        offset = indexMap[in.readUnsignedShort() + prevOffset];
        locals = new VerificationInfo[in.readUnsignedShort()];
        for (int i = 0; i < locals.length; i++) locals[i] = VerificationInfo.parse(in, cp, indexMap);
        stack = new VerificationInfo[in.readUnsignedShort()];
        for (int i = 0; i < stack.length; i++) stack[i] = VerificationInfo.parse(in, cp, indexMap);
    }

    @Override
    public void toPool(ConstantPoolBuilder cp) {
        for (VerificationInfo local : locals) local.toPool(cp);
        for (VerificationInfo stackEntry : stack) stackEntry.toPool(cp);
    }

    @Override
    public void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        out.writeByte(255);
        out.writeShort(indexMap[offset] - prevOffset);
        out.writeShort(locals.length);
        for (VerificationInfo local : locals) local.toStream(out, cp, indexMap);
        out.writeShort(stack.length);
        for (VerificationInfo stackEntry : stack) stackEntry.toStream(out, cp, indexMap);
    }

    @Override
    public int toLength() {
        int length = 7;
        for (VerificationInfo local : locals) length += local.toLength();
        for (VerificationInfo stackEntry : stack) length += stackEntry.toLength();
        return length;
    }
}
