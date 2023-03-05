package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StackMapFrameChop extends StackMapFrame {
    public int k;

    public StackMapFrameChop(int type, DataInputStream in, int prevOffset, int[] indexMap) throws IOException {
        k = 251 - type;
        offset = indexMap[in.readUnsignedShort() + prevOffset];
    }

    @Override
    public void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        out.writeByte(251 - k);
        out.writeShort(indexMap[offset] - prevOffset);
    }

    @Override
    public int toLength() {
        return 3;
    }
}
