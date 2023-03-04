package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StackMapFrameSameLocals extends StackMapFrame {
    public VerificationInfo stack;

    public StackMapFrameSameLocals(int type, DataInputStream in, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        offset = indexMap[type - 64 + prevOffset];
        stack = VerificationInfo.parse(in, cp, indexMap);
    }

    @Override
    public void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        out.writeByte(indexMap[offset] - prevOffset + 64);
        stack.toStream(out, cp, indexMap);
    }

    @Override
    public void toPool(ConstantPoolBuilder cp) {
        stack.toPool(cp);
    }

    @Override
    public int toLength() {
        return 1 + stack.toLength();
    }
}
