package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StackMapFrameSameLocalsExtended extends StackMapFrame {
    public VerificationInfo stack;

    public StackMapFrameSameLocalsExtended(DataInputStream in, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        offset = indexMap[in.readUnsignedShort() + prevOffset];
        stack = VerificationInfo.parse(in, cp, indexMap);
    }

    @Override
    public void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        out.writeByte(247);
        out.writeShort(indexMap[offset] - prevOffset);
        stack.toStream(out, cp, indexMap);
    }

    @Override
    public void toPool(ConstantPoolBuilder cp) {
        stack.toPool(cp);
    }

    @Override
    public int toLength() {
        return 3 + stack.toLength();
    }
}
