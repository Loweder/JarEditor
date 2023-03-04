package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;

import java.io.DataOutputStream;
import java.io.IOException;

public class StackMapFrameSame extends StackMapFrame {
    public StackMapFrameSame(int type, int prevOffset, int[] indexMap) {
        offset = indexMap[type + prevOffset];
    }

    @Override
    public void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        out.writeByte(indexMap[offset] - prevOffset);
    }

    @Override
    public int toLength() {
        return 1;
    }
}
