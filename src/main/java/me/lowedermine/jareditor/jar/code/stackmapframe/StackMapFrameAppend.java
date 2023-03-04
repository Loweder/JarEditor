package me.lowedermine.jareditor.jar.code.stackmapframe;

import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StackMapFrameAppend extends StackMapFrame {
    public VerificationInfo[] locals;

    public StackMapFrameAppend(int type, DataInputStream in, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        offset = indexMap[in.readUnsignedShort() + prevOffset];
        locals = new VerificationInfo[type - 251];
        for (byte i = 0; i < type - 251; i++) locals[i] = VerificationInfo.parse(in, cp, indexMap);
    }

    @Override
    public void toPool(ConstantPoolBuilder cp) {
        for (VerificationInfo local : locals) local.toPool(cp);
    }

    @Override
    public void toStream(DataOutputStream out, ConstantPool cp, int prevOffset, int[] indexMap) throws IOException {
        out.writeByte(251 + locals.length);
        out.writeShort(indexMap[offset] - prevOffset);
    }

    @Override
    public int toLength() {
        int length = 3;
        for (VerificationInfo local : locals) length += local.toLength();
        return length;
    }
}
