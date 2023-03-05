package me.lowedermine.jareditor.jar.code.instruction;

import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.infos.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class Instruction {
    public static Instruction parse(DataInputStream str, ConstantPool cp, int offset) throws IOException {
        Opcode opcode = Opcode.get(str.readUnsignedByte());
        switch (opcode) {
            case NOP:
                return new Nop();
            case ACONST_NULL:
                return new Const(Type.OBJECT, null);
            case ICONST_M1:
                return new Const(Type.INTEGER, -1);
            case ICONST_0:
                return new Const(Type.INTEGER, 0);
            case ICONST_1:
                return new Const(Type.INTEGER, 1);
            case ICONST_2:
                return new Const(Type.INTEGER, 2);
            case ICONST_3:
                return new Const(Type.INTEGER, 3);
            case ICONST_4:
                return new Const(Type.INTEGER, 4);
            case ICONST_5:
                return new Const(Type.INTEGER, 5);
            case LCONST_0:
                return new Const(Type.LONG, 0);
            case LCONST_1:
                return new Const(Type.LONG, 1);
            case FCONST_0:
                return new Const(Type.FLOAT, 0);
            case FCONST_1:
                return new Const(Type.FLOAT, 1);
            case FCONST_2:
                return new Const(Type.FLOAT, 2);
            case DCONST_0:
                return new Const(Type.DOUBLE, 0);
            case DCONST_1:
                return new Const(Type.DOUBLE, 1);
            case BIPUSH:
                return new BytePush(str);
            case SIPUSH:
                return new ShortPush(str);
            case LDC:
                return new RawLoadConst(str, cp, false);
            case LDC_W:
            case LDC_2_W:
                return new RawLoadConst(str, cp, true);
            case ILOAD:
                return new AccessLocal(Type.INTEGER, str.readUnsignedByte(), false);
            case LLOAD:
                return new AccessLocal(Type.LONG, str.readUnsignedByte(), false);
            case FLOAD:
                return new AccessLocal(Type.FLOAT, str.readUnsignedByte(), false);
            case DLOAD:
                return new AccessLocal(Type.DOUBLE, str.readUnsignedByte(), false);
            case ALOAD:
                return new AccessLocal(Type.OBJECT, str.readUnsignedByte(), false);
            case ILOAD_0:
                return new AccessLocal(Type.INTEGER, 0, false);
            case ILOAD_1:
                return new AccessLocal(Type.INTEGER, 1, false);
            case ILOAD_2:
                return new AccessLocal(Type.INTEGER, 2, false);
            case ILOAD_3:
                return new AccessLocal(Type.INTEGER, 3, false);
            case LLOAD_0:
                return new AccessLocal(Type.LONG, 0, false);
            case LLOAD_1:
                return new AccessLocal(Type.LONG, 1, false);
            case LLOAD_2:
                return new AccessLocal(Type.LONG, 2, false);
            case LLOAD_3:
                return new AccessLocal(Type.LONG, 3, false);
            case FLOAD_0:
                return new AccessLocal(Type.FLOAT, 0, false);
            case FLOAD_1:
                return new AccessLocal(Type.FLOAT, 1, false);
            case FLOAD_2:
                return new AccessLocal(Type.FLOAT, 2, false);
            case FLOAD_3:
                return new AccessLocal(Type.FLOAT, 3, false);
            case DLOAD_0:
                return new AccessLocal(Type.DOUBLE, 0, false);
            case DLOAD_1:
                return new AccessLocal(Type.DOUBLE, 1, false);
            case DLOAD_2:
                return new AccessLocal(Type.DOUBLE, 2, false);
            case DLOAD_3:
                return new AccessLocal(Type.DOUBLE, 3, false);
            case ALOAD_0:
                return new AccessLocal(Type.OBJECT, 0, false);
            case ALOAD_1:
                return new AccessLocal(Type.OBJECT, 1, false);
            case ALOAD_2:
                return new AccessLocal(Type.OBJECT, 2, false);
            case ALOAD_3:
                return new AccessLocal(Type.OBJECT, 3, false);
            case IALOAD:
                return new AccessArray(Type.INTEGER, false);
            case LALOAD:
                return new AccessArray(Type.LONG, false);
            case FALOAD:
                return new AccessArray(Type.FLOAT, false);
            case DALOAD:
                return new AccessArray(Type.DOUBLE, false);
            case AALOAD:
                return new AccessArray(Type.OBJECT, false);
            case BALOAD:
                return new AccessArray(Type.BYTE, false);
            case CALOAD:
                return new AccessArray(Type.CHAR, false);
            case SALOAD:
                return new AccessArray(Type.SHORT, false);
            case ISTORE:
                return new AccessLocal(Type.INTEGER, str.readUnsignedByte(), true);
            case LSTORE:
                return new AccessLocal(Type.LONG, str.readUnsignedByte(), true);
            case FSTORE:
                return new AccessLocal(Type.FLOAT, str.readUnsignedByte(), true);
            case DSTORE:
                return new AccessLocal(Type.DOUBLE, str.readUnsignedByte(), true);
            case ASTORE:
                return new AccessLocal(Type.OBJECT, str.readUnsignedByte(), true);
            case ISTORE_0:
                return new AccessLocal(Type.INTEGER, 0, true);
            case ISTORE_1:
                return new AccessLocal(Type.INTEGER, 1, true);
            case ISTORE_2:
                return new AccessLocal(Type.INTEGER, 2, true);
            case ISTORE_3:
                return new AccessLocal(Type.INTEGER, 3, true);
            case LSTORE_0:
                return new AccessLocal(Type.LONG, 0, true);
            case LSTORE_1:
                return new AccessLocal(Type.LONG, 1, true);
            case LSTORE_2:
                return new AccessLocal(Type.LONG, 2, true);
            case LSTORE_3:
                return new AccessLocal(Type.LONG, 3, true);
            case FSTORE_0:
                return new AccessLocal(Type.FLOAT, 0, true);
            case FSTORE_1:
                return new AccessLocal(Type.FLOAT, 1, true);
            case FSTORE_2:
                return new AccessLocal(Type.FLOAT, 2, true);
            case FSTORE_3:
                return new AccessLocal(Type.FLOAT, 3, true);
            case DSTORE_0:
                return new AccessLocal(Type.DOUBLE, 0, true);
            case DSTORE_1:
                return new AccessLocal(Type.DOUBLE, 1, true);
            case DSTORE_2:
                return new AccessLocal(Type.DOUBLE, 2, true);
            case DSTORE_3:
                return new AccessLocal(Type.DOUBLE, 3, true);
            case ASTORE_0:
                return new AccessLocal(Type.OBJECT, 0, true);
            case ASTORE_1:
                return new AccessLocal(Type.OBJECT, 1, true);
            case ASTORE_2:
                return new AccessLocal(Type.OBJECT, 2, true);
            case ASTORE_3:
                return new AccessLocal(Type.OBJECT, 3, true);
            case IASTORE:
                return new AccessArray(Type.INTEGER, true);
            case LASTORE:
                return new AccessArray(Type.LONG, true);
            case FASTORE:
                return new AccessArray(Type.FLOAT, true);
            case DASTORE:
                return new AccessArray(Type.DOUBLE, true);
            case AASTORE:
                return new AccessArray(Type.OBJECT, true);
            case BASTORE:
                return new AccessArray(Type.BYTE, true);
            case CASTORE:
                return new AccessArray(Type.CHAR, true);
            case SASTORE:
                return new AccessArray(Type.SHORT, true);
            case POP:
                return new Pop(false);
            case POP2:
                return new Pop(true);
            case DUP:
                return new Duplicate(false, 0);
            case DUP_X1:
                return new Duplicate(false, 1);
            case DUP_X2:
                return new Duplicate(false, 2);
            case DUP2:
                return new Duplicate(true, 0);
            case DUP2_X1:
                return new Duplicate(true, 1);
            case DUP2_X2:
                return new Duplicate(true, 2);
            case SWAP:
                return new Swap();
            case IADD:
                return new ALU(Operation.ADD, Type.INTEGER);
            case LADD:
                return new ALU(Operation.ADD, Type.LONG);
            case FADD:
                return new ALU(Operation.ADD, Type.FLOAT);
            case DADD:
                return new ALU(Operation.ADD, Type.DOUBLE);
            case ISUB:
                return new ALU(Operation.SUBTRACT, Type.INTEGER);
            case LSUB:
                return new ALU(Operation.SUBTRACT, Type.LONG);
            case FSUB:
                return new ALU(Operation.SUBTRACT, Type.FLOAT);
            case DSUB:
                return new ALU(Operation.SUBTRACT, Type.DOUBLE);
            case IMUL:
                return new ALU(Operation.MULTIPLY, Type.INTEGER);
            case LMUL:
                return new ALU(Operation.MULTIPLY, Type.LONG);
            case FMUL:
                return new ALU(Operation.MULTIPLY, Type.FLOAT);
            case DMUL:
                return new ALU(Operation.MULTIPLY, Type.DOUBLE);
            case IDIV:
                return new ALU(Operation.DIVIDE, Type.INTEGER);
            case LDIV:
                return new ALU(Operation.DIVIDE, Type.LONG);
            case FDIV:
                return new ALU(Operation.DIVIDE, Type.FLOAT);
            case DDIV:
                return new ALU(Operation.DIVIDE, Type.DOUBLE);
            case IREM:
                return new ALU(Operation.REMAINDER, Type.INTEGER);
            case LREM:
                return new ALU(Operation.REMAINDER, Type.LONG);
            case FREM:
                return new ALU(Operation.REMAINDER, Type.FLOAT);
            case DREM:
                return new ALU(Operation.REMAINDER, Type.DOUBLE);
            case INEG:
                return new ALU(Operation.NEGATE, Type.INTEGER);
            case LNEG:
                return new ALU(Operation.NEGATE, Type.LONG);
            case FNEG:
                return new ALU(Operation.NEGATE, Type.FLOAT);
            case DNEG:
                return new ALU(Operation.NEGATE, Type.DOUBLE);
            case ISHL:
                return new ALU(Operation.SHIFT_LEFT, Type.INTEGER);
            case LSHL:
                return new ALU(Operation.SHIFT_LEFT, Type.LONG);
            case ISHR:
                return new ALU(Operation.SHIFT_RIGHT, Type.INTEGER);
            case LSHR:
                return new ALU(Operation.SHIFT_RIGHT, Type.LONG);
            case IUSHR:
                return new ALU(Operation.LOGICAL_SHIFT_RIGHT, Type.INTEGER);
            case LUSHR:
                return new ALU(Operation.LOGICAL_SHIFT_RIGHT, Type.LONG);
            case IAND:
                return new ALU(Operation.BITWISE_AND, Type.INTEGER);
            case LAND:
                return new ALU(Operation.BITWISE_AND, Type.LONG);
            case IOR:
                return new ALU(Operation.BITWISE_OR, Type.INTEGER);
            case LOR:
                return new ALU(Operation.BITWISE_OR, Type.LONG);
            case IXOR:
                return new ALU(Operation.BITWISE_XOR, Type.INTEGER);
            case LXOR:
                return new ALU(Operation.BITWISE_XOR, Type.LONG);
            case IINC:
                return new Increment(str);
            case I2L:
                return new TypeToType(Type.INTEGER, Type.LONG);
            case I2F:
                return new TypeToType(Type.INTEGER, Type.FLOAT);
            case I2D:
                return new TypeToType(Type.INTEGER, Type.DOUBLE);
            case L2I:
                return new TypeToType(Type.LONG, Type.INTEGER);
            case L2F:
                return new TypeToType(Type.LONG, Type.FLOAT);
            case L2D:
                return new TypeToType(Type.LONG, Type.DOUBLE);
            case F2I:
                return new TypeToType(Type.FLOAT, Type.INTEGER);
            case F2L:
                return new TypeToType(Type.FLOAT, Type.LONG);
            case F2D:
                return new TypeToType(Type.FLOAT, Type.DOUBLE);
            case D2I:
                return new TypeToType(Type.DOUBLE, Type.INTEGER);
            case D2L:
                return new TypeToType(Type.DOUBLE, Type.LONG);
            case D2F:
                return new TypeToType(Type.DOUBLE, Type.FLOAT);
            case I2B:
                return new TypeToType(Type.INTEGER, Type.BYTE);
            case I2C:
                return new TypeToType(Type.INTEGER, Type.CHAR);
            case I2S:
                return new TypeToType(Type.INTEGER, Type.SHORT);
            case LCMP:
                return new Compare(Type.LONG, false);
            case FCMPL:
                return new Compare(Type.FLOAT, false);
            case FCMPG:
                return new Compare(Type.FLOAT, true);
            case DCMPL:
                return new Compare(Type.DOUBLE, false);
            case DCMPG:
                return new Compare(Type.DOUBLE, true);
            case IFEQ:
                return new If(Condition.EQUAL, Type.NONE, str);
            case IFNE:
                return new If(Condition.NOT_EQUAL, Type.NONE, str);
            case IFLT:
                return new If(Condition.LESS_THEN, Type.NONE, str);
            case IFGE:
                return new If(Condition.GREATER_THEN_OR_EQUAL, Type.NONE, str);
            case IFGT:
                return new If(Condition.GREATER_THEN, Type.NONE, str);
            case IFLE:
                return new If(Condition.LESS_THEN_OR_EQUAL, Type.NONE, str);
            case IF_ICMPEQ:
                return new If(Condition.EQUAL, Type.INTEGER, str);
            case IF_ICMPNE:
                return new If(Condition.NOT_EQUAL, Type.INTEGER, str);
            case IF_ICMPLT:
                return new If(Condition.LESS_THEN, Type.INTEGER, str);
            case IF_ICMPGE:
                return new If(Condition.GREATER_THEN_OR_EQUAL, Type.INTEGER, str);
            case IF_ICMPGT:
                return new If(Condition.GREATER_THEN, Type.INTEGER, str);
            case IF_ICMPLE:
                return new If(Condition.LESS_THEN_OR_EQUAL, Type.INTEGER, str);
            case IF_ACMPEQ:
                return new If(Condition.EQUAL, Type.OBJECT, str);
            case IF_ACMPNE:
                return new If(Condition.NOT_EQUAL, Type.OBJECT, str);
            case GOTO:
                return new Goto(false, false, str);
            case JSR:
                return new Goto(true, false, str);
            case RET:
                return new ReturnGoto(str);
            case TABLESWITCH:
                return new TableSwitch(str, offset);
            case LOOKUPSWITCH:
                return new LookupSwitch(str, offset);
            case IRETURN:
                return new Return(Type.INTEGER);
            case LRETURN:
                return new Return(Type.LONG);
            case FRETURN:
                return new Return(Type.FLOAT);
            case DRETURN:
                return new Return(Type.DOUBLE);
            case ARETURN:
                return new Return(Type.OBJECT);
            case RETURN:
                return new Return(Type.NONE);
            case GETSTATIC:
                return new AccessField(true, false, str, cp);
            case PUTSTATIC:
                return new AccessField(true, true, str, cp);
            case GETFIELD:
                return new AccessField(false, false, str, cp);
            case PUTFIELD:
                return new AccessField(false, true, str, cp);
            case INVOKEVIRTUAL:
                return new InvokeMethod(InvokeType.VIRTUAL, str, cp);
            case INVOKESPECIAL:
                return new InvokeMethod(InvokeType.SPECIAL, str, cp);
            case INVOKESTATIC:
                return new InvokeMethod(InvokeType.STATIC, str, cp);
            case INVOKEINTERFACE:
                return new InvokeMethod(InvokeType.INTERFACE, str, cp);
            case INVOKEDYNAMIC:
                return new RawInvokeDynamic(str, cp);
            case NEW:
                return new New(false, false, str, cp);
            case NEWARRAY:
                return new NewPrimitiveArray(str);
            case ANEWARRAY:
                return new New(true, false, str, cp);
            case ARRAYLENGTH:
                return new ArrayLength();
            case ATHROW:
                return new Throw();
            case CHECKCAST:
                return new CheckCast(str, cp);
            case INSTANCEOF:
                return new InstanceOf(str, cp);
            case MONITORENTER:
                return new Monitor(true);
            case MONITOREXIT:
                return new Monitor(false);
            case WIDE:
                return new Wide(str);
            case MULTIANEWARRAY:
                return new New(true, true, str, cp);
            case IFNULL:
                return new If(Condition.NULL, Type.NONE, str);
            case IFNONNULL:
                return new If(Condition.NOT_NULL, Type.NONE, str);
            case GOTO_W:
                return new Goto(false, true, str);
            case JSR_W:
                return new Goto(true, true, str);
            case BREAKPOINT:
                return new BreakPoint();
            case IMPDEP1:
                return new ImpDep(false);
            case IMPDEP2:
                return new ImpDep(true);
        }
        return null;
    }

    public static int parseLengthRaw(DataInputStream str, int offset) throws IOException {
        Opcode opcode = Opcode.get(str.readUnsignedByte());
        switch (opcode) {
            case NOP:
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case LCONST_0:
            case LCONST_1:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case DCONST_0:
            case DCONST_1:
            case ILOAD_0:
            case ILOAD_1:
            case ILOAD_2:
            case ILOAD_3:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case ISTORE_0:
            case ISTORE_1:
            case ISTORE_2:
            case ISTORE_3:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
            case POP:
            case POP2:
            case DUP:
            case DUP_X1:
            case DUP_X2:
            case DUP2:
            case DUP2_X1:
            case DUP2_X2:
            case SWAP:
            case IADD:
            case LADD:
            case FADD:
            case DADD:
            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
            case IREM:
            case LREM:
            case FREM:
            case DREM:
            case INEG:
            case LNEG:
            case FNEG:
            case DNEG:
            case ISHL:
            case LSHL:
            case ISHR:
            case LSHR:
            case IUSHR:
            case LUSHR:
            case IAND:
            case LAND:
            case IOR:
            case LOR:
            case IXOR:
            case LXOR:
            case I2L:
            case I2F:
            case I2D:
            case L2I:
            case L2F:
            case L2D:
            case F2I:
            case F2L:
            case F2D:
            case D2I:
            case D2L:
            case D2F:
            case I2B:
            case I2C:
            case I2S:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
            case ARRAYLENGTH:
            case ATHROW:
            case MONITORENTER:
            case MONITOREXIT:
            case BREAKPOINT:
            case IMPDEP1:
            case IMPDEP2:
                return 1;
            case BIPUSH:
            case LDC:
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case ALOAD:
            case DLOAD:
            case ISTORE:
            case FSTORE:
            case LSTORE:
            case DSTORE:
            case ASTORE:
            case RET:
            case NEWARRAY:
                str.readUnsignedByte();
                return 2;
            case SIPUSH:
            case LDC_W:
            case LDC_2_W:
            case IINC:
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case GOTO:
            case JSR:
            case GETSTATIC:
            case PUTSTATIC:
            case GETFIELD:
            case PUTFIELD:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case NEW:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
            case IFNULL:
            case IFNONNULL:
                str.readUnsignedShort();
                return 3;
            case MULTIANEWARRAY:
                str.readUnsignedShort();
                str.readUnsignedByte();
                return 4;
            case INVOKEINTERFACE:
            case INVOKEDYNAMIC:
            case GOTO_W:
            case JSR_W:
                str.readInt();
                return 5;
            case TABLESWITCH:
                return TableSwitch.parseLength(str, offset);
            case LOOKUPSWITCH:
                return LookupSwitch.parseLength(str, offset);
            case WIDE:
                Opcode wideOpcode = Opcode.get(str.readUnsignedByte());
                if (wideOpcode == Opcode.IINC) {
                    str.readInt();
                    str.readUnsignedByte();
                    return 6;
                }
                str.readUnsignedShort();
                str.readUnsignedByte();
                return 4;
        }
        return 0;
    }

    public int toLength(ConstantPool cp) {
        if (this instanceof Nop ||
                this instanceof Const ||
                this instanceof AccessArray ||
                this instanceof Pop ||
                this instanceof Duplicate ||
                this instanceof Swap ||
                this instanceof ALU ||
                this instanceof TypeToType ||
                this instanceof Compare ||
                this instanceof Return ||
                this instanceof ArrayLength ||
                this instanceof Throw ||
                this instanceof Monitor ||
                this instanceof BreakPoint ||
                this instanceof ImpDep) {
            return 1;
        } else if (this instanceof BytePush ||
                this instanceof NewPrimitiveArray ||
                this instanceof ReturnGoto) {
            return 2;
        } else if (this instanceof New && ((New)this).dimensions <= 1 ||
                this instanceof Increment ||
                this instanceof ShortPush ||
                this instanceof If ||
                this instanceof AccessField ||
                (this instanceof InvokeMethod && ((InvokeMethod) this).type != InvokeType.INTERFACE) ||
                this instanceof CheckCast ||
                this instanceof InstanceOf) {
            return 3;
        } else if (this instanceof New && ((New)this).dimensions > 1) {
            return 4;
        } else if ((this instanceof InvokeMethod && ((InvokeMethod) this).type == InvokeType.INTERFACE) ||
                this instanceof RawInvokeDynamic) {
            return 5;
        } else if (this instanceof AccessLocal) {
            int index = ((AccessLocal) this).index;
            if (index < 4) {
                return 1;
            } else {
                return 2;
            }
        } else if (this instanceof RawLoadConst) {
            int index = cp.indexOf(((RawLoadConst) this).constant);
            if (((RawLoadConst) this).wide) {
                return 3;
            } else {
                if (index < 256) {
                    return 2;
                } else {
                    return 3;
                }
            }
        } else if (this instanceof Goto) {
            boolean wide = ((Goto) this).wide;
            if (!wide)
                return 3;
            else
                return 5;
        } else if (this instanceof Wide) {
            boolean isIncrement = ((Wide) this).isIncrement;
            if (!isIncrement) {
                return 4;
            } else {
                return 6;
            }
        } else if (this instanceof TableSwitch) {
            return 13 + ((TableSwitch) this).padding + (((TableSwitch) this).caseOffsets.length * 4);
        } else if (this instanceof LookupSwitch) {
            return 9 + ((LookupSwitch) this).padding + (((LookupSwitch) this).pairs.length * 8);
        }
        return 0;
    }

    public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
        if (this instanceof Nop) {
            Opcode.NOP.write(out);
        } else if (this instanceof Const) {
            Type type = ((Const) this).type;
            if (type == Type.INTEGER) {
                int value = (int) ((Const) this).value;
                if (value == -1) Opcode.ICONST_M1.write(out);
                else if (value == 0) Opcode.ICONST_0.write(out);
                else if (value == 1) Opcode.ICONST_1.write(out);
                else if (value == 2) Opcode.ICONST_2.write(out);
                else if (value == 3) Opcode.ICONST_3.write(out);
                else if (value == 4) Opcode.ICONST_4.write(out);
                else if (value == 5) Opcode.ICONST_5.write(out);
            } else if (type == Type.LONG) {
                long value = (long) ((Const) this).value;
                if (value == 0) Opcode.LCONST_0.write(out);
                else if (value == 1) Opcode.LCONST_1.write(out);
            } else if (type == Type.FLOAT) {
                float value = (int) ((Const) this).value;
                if (value == 0) Opcode.FCONST_0.write(out);
                else if (value == 1) Opcode.FCONST_1.write(out);
                else if (value == 2) Opcode.FCONST_2.write(out);
            } else if (type == Type.DOUBLE) {
                double value = (double) ((Const) this).value;
                if (value == 0) Opcode.DCONST_0.write(out);
                else if (value == 1) Opcode.DCONST_1.write(out);
            } else if (type == Type.OBJECT) {
                Opcode.ACONST_NULL.write(out);
            }
        } else if (this instanceof BytePush) {
            Opcode.BIPUSH.write(out);
            out.writeByte(((BytePush) this).value);
        } else if (this instanceof ShortPush) {
            Opcode.SIPUSH.write(out);
            out.writeShort(((ShortPush) this).value);
        } else if (this instanceof RawLoadConst) {
            int index = cp.indexOf(((RawLoadConst) this).constant);
            if (((RawLoadConst) this).wide) {
                Opcode.LDC_2_W.write(out);
                out.writeShort(index);
            } else {
                if (index > 255) {
                    Opcode.LDC_W.write(out);
                    out.writeShort(index);
                } else {
                    Opcode.LDC.write(out);
                    out.writeByte(index);
                }
            }
        } else if (this instanceof AccessLocal) {
            int index = ((AccessLocal) this).index;
            Type type = ((AccessLocal) this).type;
            if (((AccessLocal) this).put) {
                if (type == Type.INTEGER) {
                    if (index == 0) {
                        Opcode.ISTORE_0.write(out);
                    } else if (index == 1) {
                        Opcode.ISTORE_1.write(out);
                    } else if (index == 2) {
                        Opcode.ISTORE_2.write(out);
                    } else if (index == 3) {
                        Opcode.ISTORE_3.write(out);
                    } else {
                        Opcode.ISTORE.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.LONG) {
                    if (index == 0) {
                        Opcode.LSTORE_0.write(out);
                    } else if (index == 1) {
                        Opcode.LSTORE_1.write(out);
                    } else if (index == 2) {
                        Opcode.LSTORE_2.write(out);
                    } else if (index == 3) {
                        Opcode.LSTORE_3.write(out);
                    } else {
                        Opcode.LSTORE.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.FLOAT) {
                    if (index == 0) {
                        Opcode.FSTORE_0.write(out);
                    } else if (index == 1) {
                        Opcode.FSTORE_1.write(out);
                    } else if (index == 2) {
                        Opcode.FSTORE_2.write(out);
                    } else if (index == 3) {
                        Opcode.FSTORE_3.write(out);
                    } else {
                        Opcode.FSTORE.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.DOUBLE) {
                    if (index == 0) {
                        Opcode.DSTORE_0.write(out);
                    } else if (index == 1) {
                        Opcode.DSTORE_1.write(out);
                    } else if (index == 2) {
                        Opcode.DSTORE_2.write(out);
                    } else if (index == 3) {
                        Opcode.DSTORE_3.write(out);
                    } else {
                        Opcode.DSTORE.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.OBJECT) {
                    if (index == 0) {
                        Opcode.ASTORE_0.write(out);
                    } else if (index == 1) {
                        Opcode.ASTORE_1.write(out);
                    } else if (index == 2) {
                        Opcode.ASTORE_2.write(out);
                    } else if (index == 3) {
                        Opcode.ASTORE_3.write(out);
                    } else {
                        Opcode.ASTORE.write(out);
                        out.writeByte(index);
                    }
                }
            } else {
                if (type == Type.INTEGER) {
                    if (index == 0) {
                        Opcode.ILOAD_0.write(out);
                    } else if (index == 1) {
                        Opcode.ILOAD_1.write(out);
                    } else if (index == 2) {
                        Opcode.ILOAD_2.write(out);
                    } else if (index == 3) {
                        Opcode.ILOAD_3.write(out);
                    } else {
                        Opcode.ILOAD.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.LONG) {
                    if (index == 0) {
                        Opcode.LLOAD_0.write(out);
                    } else if (index == 1) {
                        Opcode.LLOAD_1.write(out);
                    } else if (index == 2) {
                        Opcode.LLOAD_2.write(out);
                    } else if (index == 3) {
                        Opcode.LLOAD_3.write(out);
                    } else {
                        Opcode.LLOAD.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.FLOAT) {
                    if (index == 0) {
                        Opcode.FLOAD_0.write(out);
                    } else if (index == 1) {
                        Opcode.FLOAD_1.write(out);
                    } else if (index == 2) {
                        Opcode.FLOAD_2.write(out);
                    } else if (index == 3) {
                        Opcode.FLOAD_3.write(out);
                    } else {
                        Opcode.FLOAD.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.DOUBLE) {
                    if (index == 0) {
                        Opcode.DLOAD_0.write(out);
                    } else if (index == 1) {
                        Opcode.DLOAD_1.write(out);
                    } else if (index == 2) {
                        Opcode.DLOAD_2.write(out);
                    } else if (index == 3) {
                        Opcode.DLOAD_3.write(out);
                    } else {
                        Opcode.DLOAD.write(out);
                        out.writeByte(index);
                    }
                } else if (type == Type.OBJECT) {
                    if (index == 0) {
                        Opcode.ALOAD_0.write(out);
                    } else if (index == 1) {
                        Opcode.ALOAD_1.write(out);
                    } else if (index == 2) {
                        Opcode.ALOAD_2.write(out);
                    } else if (index == 3) {
                        Opcode.ALOAD_3.write(out);
                    } else {
                        Opcode.ALOAD.write(out);
                        out.writeByte(index);
                    }
                }
            }
        } else if (this instanceof AccessArray) {
            Type type = ((AccessArray) this).type;
            if (((AccessArray) this).put) {
                if (type == Type.INTEGER) {
                    Opcode.IASTORE.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LASTORE.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FASTORE.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DASTORE.write(out);
                } else if (type == Type.OBJECT) {
                    Opcode.AASTORE.write(out);
                } else if (type == Type.BYTE) {
                    Opcode.BASTORE.write(out);
                } else if (type == Type.CHAR) {
                    Opcode.CASTORE.write(out);
                } else if (type == Type.SHORT) {
                    Opcode.SASTORE.write(out);
                }
            } else {
                if (type == Type.INTEGER) {
                    Opcode.IALOAD.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LALOAD.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FALOAD.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DALOAD.write(out);
                } else if (type == Type.OBJECT) {
                    Opcode.AALOAD.write(out);
                } else if (type == Type.BYTE) {
                    Opcode.BALOAD.write(out);
                } else if (type == Type.CHAR) {
                    Opcode.CALOAD.write(out);
                } else if (type == Type.SHORT) {
                    Opcode.SALOAD.write(out);
                }
            }
        } else if (this instanceof Pop) {
            if (((Pop) this).wide) Opcode.POP2.write(out);
            else Opcode.POP.write(out);
        } else if (this instanceof Duplicate) {
            if (!((Duplicate) this).wide) {
                if (((Duplicate) this).mode == 0) {
                    Opcode.DUP.write(out);
                } else if (((Duplicate) this).mode == 1) {
                    Opcode.DUP_X1.write(out);
                } else if (((Duplicate) this).mode == 2) {
                    Opcode.DUP_X2.write(out);
                }
            } else {
                if (((Duplicate) this).mode == 0) {
                    Opcode.DUP2.write(out);
                } else if (((Duplicate) this).mode == 1) {
                    Opcode.DUP2_X1.write(out);
                } else if (((Duplicate) this).mode == 2) {
                    Opcode.DUP2_X2.write(out);
                }
            }
        } else if (this instanceof Swap) {
            Opcode.SWAP.write(out);
        } else if (this instanceof ALU) {
            Type type = ((ALU) this).type;
            Operation mode = ((ALU) this).operation;
            if (mode == Operation.ADD) {
                if (type == Type.INTEGER) {
                    Opcode.IADD.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LADD.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FADD.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DADD.write(out);
                }
            } else if (mode == Operation.SUBTRACT) {
                if (type == Type.INTEGER) {
                    Opcode.ISUB.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LSUB.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FSUB.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DSUB.write(out);
                }
            } else if (mode == Operation.MULTIPLY) {
                if (type == Type.INTEGER) {
                    Opcode.IMUL.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LMUL.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FMUL.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DMUL.write(out);
                }
            } else if (mode == Operation.DIVIDE) {
                if (type == Type.INTEGER) {
                    Opcode.IDIV.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LDIV.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FDIV.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DDIV.write(out);
                }
            } else if (mode == Operation.REMAINDER) {
                if (type == Type.INTEGER) {
                    Opcode.IREM.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LREM.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FREM.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DREM.write(out);
                }
            } else if (mode == Operation.NEGATE) {
                if (type == Type.INTEGER) {
                    Opcode.INEG.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LNEG.write(out);
                } else if (type == Type.FLOAT) {
                    Opcode.FNEG.write(out);
                } else if (type == Type.DOUBLE) {
                    Opcode.DNEG.write(out);
                }
            } else if (mode == Operation.SHIFT_LEFT) {
                if (type == Type.INTEGER) {
                    Opcode.ISHL.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LSHL.write(out);
                }
            } else if (mode == Operation.SHIFT_RIGHT) {
                if (type == Type.INTEGER) {
                    Opcode.ISHR.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LSHR.write(out);
                }
            } else if (mode == Operation.LOGICAL_SHIFT_RIGHT) {
                if (type == Type.INTEGER) {
                    Opcode.IUSHR.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LUSHR.write(out);
                }
            } else if (mode == Operation.BITWISE_AND) {
                if (type == Type.INTEGER) {
                    Opcode.IAND.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LAND.write(out);
                }
            } else if (mode == Operation.BITWISE_OR) {
                if (type == Type.INTEGER) {
                    Opcode.IOR.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LOR.write(out);
                }
            } else if (mode == Operation.BITWISE_XOR) {
                if (type == Type.INTEGER) {
                    Opcode.IXOR.write(out);
                } else if (type == Type.LONG) {
                    Opcode.LXOR.write(out);
                }
            }
        } else if (this instanceof Increment) {
            Opcode.IINC.write(out);
            out.writeByte(((Increment) this).index);
            out.writeByte(((Increment) this).count);
        } else if (this instanceof TypeToType) {
            Type from = ((TypeToType) this).from;
            Type to = ((TypeToType) this).to;
            if (from == Type.INTEGER) {
                if (to == Type.LONG) {
                    Opcode.I2L.write(out);
                } else if (to == Type.FLOAT) {
                    Opcode.I2F.write(out);
                } else if (to == Type.DOUBLE) {
                    Opcode.I2D.write(out);
                } else if (to == Type.BYTE) {
                    Opcode.I2B.write(out);
                } else if (to == Type.CHAR) {
                    Opcode.I2C.write(out);
                } else if (to == Type.SHORT) {
                    Opcode.I2S.write(out);
                }
            } else if (from == Type.LONG) {
                if (to == Type.INTEGER) {
                    Opcode.L2I.write(out);
                } else if (to == Type.FLOAT) {
                    Opcode.L2F.write(out);
                } else if (to == Type.DOUBLE) {
                    Opcode.L2D.write(out);
                }
            } else if (from == Type.FLOAT) {
                if (to == Type.INTEGER) {
                    Opcode.F2I.write(out);
                } else if (to == Type.LONG) {
                    Opcode.F2L.write(out);
                } else if (to == Type.DOUBLE) {
                    Opcode.F2D.write(out);
                }
            } else if (from == Type.DOUBLE) {
                if (to == Type.INTEGER) {
                    Opcode.D2I.write(out);
                } else if (to == Type.LONG) {
                    Opcode.D2L.write(out);
                } else if (to == Type.FLOAT) {
                    Opcode.D2F.write(out);
                }
            }
        } else if (this instanceof Compare) {
            Type type = ((Compare) this).type;
            boolean positiveOnNAN = ((Compare) this).positiveOnNAN;
            if (type == Type.LONG)
                Opcode.LCMP.write(out);
            else if (type == Type.DOUBLE) {
                if (positiveOnNAN) Opcode.DCMPG.write(out);
                else Opcode.DCMPL.write(out);
            } else if (type == Type.FLOAT) {
                if (positiveOnNAN) Opcode.FCMPG.write(out);
                else Opcode.FCMPL.write(out);
            }
        } else if (this instanceof If) {
            Type type = ((If) this).type;
            Condition condition = ((If) this).condition;
            if (condition == Condition.EQUAL) {
                if (type == Type.INTEGER) {
                    Opcode.IF_ICMPEQ.write(out);
                } else if (type == Type.OBJECT) {
                    Opcode.IF_ACMPEQ.write(out);
                } else {
                    Opcode.IFEQ.write(out);
                }
            } else if (condition == Condition.NOT_EQUAL) {
                if (type == Type.INTEGER) {
                    Opcode.IF_ICMPNE.write(out);
                } else if (type == Type.OBJECT) {
                    Opcode.IF_ACMPNE.write(out);
                } else {
                    Opcode.IFNE.write(out);
                }
            } else if (condition == Condition.LESS_THEN) {
                if (type == Type.INTEGER) {
                    Opcode.IF_ICMPLT.write(out);
                } else {
                    Opcode.IFLT.write(out);
                }
            } else if (condition == Condition.GREATER_THEN_OR_EQUAL) {
                if (type == Type.INTEGER) {
                    Opcode.IF_ICMPGE.write(out);
                } else {
                    Opcode.IFGE.write(out);
                }
            } else if (condition == Condition.GREATER_THEN) {
                if (type == Type.INTEGER) {
                    Opcode.IF_ICMPGT.write(out);
                } else {
                    Opcode.IFGT.write(out);
                }
            } else if (condition == Condition.LESS_THEN_OR_EQUAL) {
                if (type == Type.INTEGER) {
                    Opcode.IF_ICMPLE.write(out);
                } else {
                    Opcode.IFLE.write(out);
                }
            } else if (condition == Condition.NULL) {
                Opcode.IFNULL.write(out);
            } else if (condition == Condition.NOT_NULL) {
                Opcode.IFNONNULL.write(out);
            }
            out.writeShort(((If) this).offset);
        } else if (this instanceof Goto) {
            boolean isJump = ((Goto) this).isJump;
            boolean wide = ((Goto) this).wide;
            if (isJump) {
                if (wide)
                    Opcode.JSR_W.write(out);
                else
                    Opcode.JSR.write(out);
            } else {
                if (wide)
                    Opcode.GOTO_W.write(out);
                else
                    Opcode.GOTO.write(out);
            }
            if (wide)
                out.writeInt(((Goto) this).offset);
            else
                out.writeShort(((Goto) this).offset);
        } else if (this instanceof ReturnGoto) {
            Opcode.RET.write(out);
            out.writeByte(((ReturnGoto) this).local);
        } else if (this instanceof TableSwitch) {
            Opcode.TABLESWITCH.write(out);
            byte[] padding = new byte[((TableSwitch) this).padding];
            out.write(padding);
            out.writeInt(((TableSwitch) this).defaultOffset);
            out.writeInt(((TableSwitch) this).lowRange);
            out.writeInt(((TableSwitch) this).highRange);
            for (int caseOffset : ((TableSwitch) this).caseOffsets) out.writeInt(caseOffset);
        } else if (this instanceof LookupSwitch) {
            Opcode.LOOKUPSWITCH.write(out);
            byte[] padding = new byte[((LookupSwitch) this).padding];
            out.write(padding);
            out.writeInt(((LookupSwitch) this).defaultOffset);
            out.writeInt(((LookupSwitch) this).pairsCount);
            for (LookupSwitch.Pair pair : ((LookupSwitch) this).pairs) {
                out.writeInt(pair.match);
                out.writeInt(pair.offset);
            }
        } else if (this instanceof Return) {
            Type type = ((Return) this).type;
            if (type == Type.INTEGER) {
                Opcode.IRETURN.write(out);
            } else if (type == Type.LONG) {
                Opcode.LRETURN.write(out);
            } else if (type == Type.FLOAT) {
                Opcode.FRETURN.write(out);
            } else if (type == Type.DOUBLE) {
                Opcode.DRETURN.write(out);
            } else if (type == Type.OBJECT) {
                Opcode.ARETURN.write(out);
            } else if (type == Type.NONE) {
                Opcode.RETURN.write(out);
            }
        } else if (this instanceof AccessField) {
            if (((AccessField) this).put) {
                if (((AccessField) this).isStatic) {
                    Opcode.PUTSTATIC.write(out);
                } else {
                    Opcode.PUTFIELD.write(out);
                }
            } else {
                if (((AccessField) this).isStatic) {
                    Opcode.GETSTATIC.write(out);
                } else {
                    Opcode.GETFIELD.write(out);
                }
            }
            out.writeShort(cp.indexOf(((AccessField) this).info));
        } else if (this instanceof InvokeMethod) {
            switch (((InvokeMethod) this).type) {
                case VIRTUAL:
                    Opcode.INVOKEVIRTUAL.write(out);
                    break;
                case SPECIAL:
                    Opcode.INVOKESPECIAL.write(out);
                    break;
                case STATIC:
                    Opcode.INVOKESTATIC.write(out);
                    break;
                case INTERFACE:
                    Opcode.INVOKEINTERFACE.write(out);
                    break;
            }
            out.writeShort(cp.indexOf(((InvokeMethod) this).info));
            if (((InvokeMethod) this).type == InvokeType.INTERFACE) {
                out.writeByte(((InvokeMethod) this).count);
                out.writeByte(0);
            }
        } else if (this instanceof RawInvokeDynamic) {
            Opcode.INVOKEDYNAMIC.write(out);
            out.writeShort(cp.indexOf(((RawInvokeDynamic) this).info));
            out.writeShort(0);
        } else if (this instanceof New) {
            if (((New) this).array) {
                if (((New) this).dimensions > 1)
                    Opcode.MULTIANEWARRAY.write(out);
                else
                    Opcode.ANEWARRAY.write(out);
            } else
                Opcode.NEW.write(out);
            out.writeShort(cp.indexOf(((New) this).info));
            if (((New) this).dimensions > 1) {
                out.writeByte(((New) this).dimensions);
            }
        } else if (this instanceof NewPrimitiveArray) {
            Opcode.NEWARRAY.write(out);
            out.writeByte(((NewPrimitiveArray) this).type);
        } else if (this instanceof ArrayLength) {
            Opcode.ARRAYLENGTH.write(out);
        } else if (this instanceof Throw) {
            Opcode.ATHROW.write(out);
        } else if (this instanceof CheckCast) {
            Opcode.CHECKCAST.write(out);
            out.writeShort(cp.indexOf(((CheckCast) this).info));
        } else if (this instanceof InstanceOf) {
            Opcode.INSTANCEOF.write(out);
            out.writeShort(cp.indexOf(((InstanceOf) this).info));
        } else if (this instanceof Monitor) {
            if (((Monitor) this).enter) Opcode.MONITORENTER.write(out);
            else Opcode.MONITOREXIT.write(out);
        } else if (this instanceof Wide) {
            Opcode.WIDE.write(out);
            int index = ((Wide) this).index;
            boolean isIncrement = ((Wide) this).isIncrement;
            if (isIncrement) {
                Opcode.IINC.write(out);
                out.writeShort(index);
                out.writeShort(((Wide) this).count);
            } else {
                ((Wide) this).opcode.write(out);
                out.writeShort(index);
            }
        } else if (this instanceof BreakPoint)
            Opcode.BREAKPOINT.write(out);
        else if (this instanceof ImpDep) {
            if (((ImpDep) this).second)
                Opcode.IMPDEP1.write(out);
            else
                Opcode.IMPDEP2.write(out);
        }
    }

    public void toPool(ConstantPoolBuilder cp) {}

    public static class Nop extends Instruction {

    }

    public static class Const extends Instruction {
        public Type type;
        public Object value;

        public Const(Type type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

    public static class BytePush extends Instruction {
        public int value;

        public BytePush(DataInputStream str) throws IOException {
            value = str.readUnsignedByte();
        }
    }

    public static class ShortPush extends Instruction {
        public int value;

        public ShortPush(DataInputStream str) throws IOException {
            value = str.readUnsignedShort();
        }
    }

    public static class RawLoadConst extends Instruction {
        public Object constant;
        public boolean wide;

        protected RawLoadConst() {

        }

        public RawLoadConst(DataInputStream str, ConstantPool cp, boolean readWide) throws IOException {
            int index = readWide ? str.readUnsignedShort() : str.readUnsignedByte();
            constant = cp.ro(index);
            wide = constant instanceof Double || constant instanceof Long;
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {}
    }

    public static class LoadConst extends Instruction {
        public Object constant;
        public boolean wide;
        public FieldInfo info = null;

        public LoadConst(RawLoadConst raw, ClassFile.BootstrapMethod[] methods) {
            if (raw.constant instanceof BootstrapFieldInfo) {
                constant = methods[((BootstrapFieldInfo) raw.constant).index];
                info = ((BootstrapFieldInfo) raw.constant).getInfo();
            } else
                constant = raw.constant;
            wide = raw.wide;
        }

        public RawLoadConst toRaw(List<ClassFile.BootstrapMethod> methods) {
            RawLoadConst newRaw = new RawLoadConst();
            newRaw.wide = wide;
            if (!(constant instanceof ClassFile.BootstrapMethod)) {
                newRaw.constant = constant;
            } else {
                ClassFile.BootstrapMethod fConstant = (ClassFile.BootstrapMethod) constant;
                if (!methods.contains(fConstant)) methods.add(fConstant);
                newRaw.constant = new BootstrapFieldInfo(methods.indexOf(fConstant), info);
            }
            return newRaw;
        }

        public void toPool(ConstantPoolBuilder cp, List<ClassFile.BootstrapMethod> methods) {
            if (!(constant instanceof ClassFile.BootstrapMethod))
                cp.add(constant);
            else {
                ClassFile.BootstrapMethod fConstant = (ClassFile.BootstrapMethod) constant;
                cp.add("BootstrapMethods");
                fConstant.toPool(cp);
                if (!methods.contains(fConstant)) methods.add(fConstant);
                cp.add(new BootstrapFieldInfo(methods.indexOf(fConstant), info));
            }
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {}
    }

    public static class AccessLocal extends Instruction {
        public Type type;
        public int index;
        public boolean put;

        public AccessLocal(Type type, int index, boolean put) {
            this.type = type;
            this.index = index;
            this.put = put;
        }
    }

    public static class AccessArray extends Instruction {
        public Type type;
        public boolean put;

        public AccessArray(Type type, boolean put) {
            this.type = type;
            this.put = put;
        }
    }

    public static class Pop extends Instruction {
        public boolean wide;

        public Pop(boolean wide) {
            this.wide = wide;
        }
    }

    public static class Duplicate extends Instruction {
        public boolean wide;
        public int mode;

        public Duplicate(boolean wide, int mode) {
            this.wide = wide;
            this.mode = mode;
        }
    }

    public static class Swap extends Instruction {
    }

    public static class ALU extends Instruction {
        public Operation operation;
        public Type type;

        public ALU(Operation operation, Type type) {
            this.operation = operation;
            this.type = type;
        }
    }

    public static class Increment extends Instruction {
        public int index;
        public int count;

        public Increment(DataInputStream str) throws IOException {
            index = str.readUnsignedByte();
            count = str.readUnsignedByte();
        }
    }

    public static class TypeToType extends Instruction {
        public Type from;
        public Type to;

        public TypeToType(Type from, Type to) {
            this.from = from;
            this.to = to;
        }
    }

    public static class Compare extends Instruction {
        public Type type;
        public boolean positiveOnNAN;

        public Compare(Type type, boolean positiveOnNAN) {
            this.type = type;
            this.positiveOnNAN = positiveOnNAN;
        }
    }

    public static class If extends Instruction {
        public Condition condition;
        public Type type;
        public int offset;

        public If(Condition condition, Type type, DataInputStream str) throws IOException {
            this.condition = condition;
            this.type = type;
            offset = str.readUnsignedShort();
        }
    }

    public static class Goto extends Instruction {
        public boolean isJump;
        public boolean wide;
        public int offset;

        public Goto(boolean isJump, boolean wide, DataInputStream str) throws IOException {
            this.offset = wide ? str.readInt() : str.readUnsignedShort();
            this.isJump = isJump;
            this.wide = wide;
        }
    }

    public static class ReturnGoto extends Instruction {
        public int local;

        public ReturnGoto(DataInputStream str) throws IOException {
            local = str.readUnsignedByte();
        }
    }

    public static class TableSwitch extends Instruction {
        public int defaultOffset;
        public int lowRange;
        public int highRange;
        public int[] caseOffsets;
        public int padding;

        public TableSwitch(DataInputStream str, int offset) throws IOException {
            padding = 3 - (offset % 4);
            byte[] buff = new byte[padding];
            int ignored = str.read(buff, 0, padding);
            defaultOffset = str.readInt();
            lowRange = str.readInt();
            highRange = str.readInt();
            caseOffsets = new int[highRange - lowRange + 1];
            for (int i = 0; i < highRange - lowRange + 1; i++) {
                caseOffsets[i] = str.readInt();
            }
        }

        public static int parseLength(DataInputStream str, int offset) throws IOException {
            int padding = 3 - (offset % 4);
            byte[] buff = new byte[padding + 4];
            {
                int ignored = str.read(buff, 0, padding + 4);
            }
            int lowRange = str.readInt();
            int highRange = str.readInt();
            buff = new byte[(highRange - lowRange + 1) * 4];
            {
                int ignored = str.read(buff, 0, (highRange - lowRange + 1) * 4);
            }
            return 12 + padding + (highRange - lowRange + 1) * 4;
        }
    }

    public static class LookupSwitch extends Instruction {
        public int defaultOffset;
        public int pairsCount;
        public Pair[] pairs;
        public int padding;

        public LookupSwitch(DataInputStream str, int offset) throws IOException {
            padding = 3 - (offset % 4);
            byte[] buff = new byte[padding];
            int ignored = str.read(buff, 0, padding);
            defaultOffset = str.readInt();
            pairsCount = str.readInt();
            pairs = new Pair[pairsCount];
            for (int i = 0; i < pairsCount; i++) {
                pairs[i] = new Pair(str.readInt(), str.readInt());
            }
        }

        public static int parseLength(DataInputStream str, int offset) throws IOException {
            int padding = 3 - (offset % 4);
            byte[] buff = new byte[padding + 4];
            int ignored = str.read(buff, 0, padding + 4);
            int pairs = str.readInt();
            return 8 + padding + pairs * 8;
        }

        public static class Pair {
            public int match;
            public int offset;

            public Pair(int match, int offset) {
                this.match = match;
                this.offset = offset;
            }
        }
    }

    public static class Return extends Instruction {
        public Type type;

        public Return(Type type) {
            this.type = type;
        }
    }

    public static class AccessField extends Instruction {
        public ClassFieldInfo info;
        public boolean isStatic;
        public boolean put;

        public AccessField(boolean isStatic, boolean put, DataInputStream str, ConstantPool cp) throws IOException {
            info = (ClassFieldInfo) cp.ro(str.readUnsignedShort());
            this.isStatic = isStatic;
            this.put = put;
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
        }
    }

    public static class InvokeMethod extends Instruction {
        public ClassMethodInfo info;
        public int count = 0;
        public InvokeType type;

        public InvokeMethod(InvokeType type, DataInputStream str, ConstantPool cp) throws IOException {
            info = (ClassMethodInfo) cp.ro(str.readUnsignedShort());
            this.type = type;
            if (type == InvokeType.INTERFACE) {
                count = str.readUnsignedByte();
                str.readUnsignedByte();
            }
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
        }
    }

    public static class RawInvokeDynamic extends Instruction {
        public BootstrapMethodInfo info;

        protected RawInvokeDynamic() {

        }

        public RawInvokeDynamic(DataInputStream str, ConstantPool cp) throws IOException {
            info = (BootstrapMethodInfo) cp.ro(str.readUnsignedShort());
            str.readUnsignedShort();
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
        }
    }

    public static class InvokeDynamic extends Instruction {
        public ClassFile.BootstrapMethod method;
        public MethodInfo info;

        public InvokeDynamic(RawInvokeDynamic raw, ClassFile.BootstrapMethod[] methods) {
            method = methods[raw.info.index].clone();
            info = raw.info.getInfo();
        }

        public RawInvokeDynamic toRaw(List<ClassFile.BootstrapMethod> methods) {
            RawInvokeDynamic newRaw = new RawInvokeDynamic();
            if (!methods.contains(method)) methods.add(method);
            newRaw.info = new BootstrapMethodInfo(methods.indexOf(method), info);
            return newRaw;
        }

        public void toPool(ConstantPoolBuilder cp, List<ClassFile.BootstrapMethod> methods) {
            cp.add("BootstrapMethods");
            method.toPool(cp);
            if (!methods.contains(method)) methods.add(method);
            cp.add(new BootstrapMethodInfo(methods.indexOf(method), info));
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {}
    }

    public static class New extends Instruction {
        public ClassInfo info;
        public boolean array;
        public int dimensions = 1;

        public New(boolean array, boolean multiDim, DataInputStream str, ConstantPool cp) throws IOException {
            info = (ClassInfo) cp.ro(str.readUnsignedShort());
            this.array = array;
            if (array && multiDim) dimensions = str.readUnsignedByte();
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
        }
    }

    public static class NewPrimitiveArray extends Instruction {
        public int type;

        public NewPrimitiveArray(DataInputStream str) throws IOException {
            type = str.readUnsignedByte();
        }
    }

    public static class ArrayLength extends Instruction {
    }

    public static class Throw extends Instruction {
    }

    public static class CheckCast extends Instruction {
        public ClassInfo info;

        public CheckCast(DataInputStream str, ConstantPool cp) throws IOException {
            info = (ClassInfo) cp.ro(str.readUnsignedShort());
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
        }
    }

    public static class InstanceOf extends Instruction {
        public ClassInfo info;

        public InstanceOf(DataInputStream str, ConstantPool cp) throws IOException {
            info = (ClassInfo) cp.ro(str.readUnsignedShort());
        }

        @Override
        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
        }
    }

    public static class Monitor extends Instruction {
        public boolean enter;

        public Monitor(boolean enter) {
            this.enter = enter;
        }
    }

    public static class Wide extends Instruction {
        public boolean isIncrement;
        public Opcode opcode;
        public int index;
        public int count = 0;

        public Wide(DataInputStream str) throws IOException {
            opcode = Opcode.get(str.readUnsignedByte());
            index = str.readUnsignedShort();
            if (Opcode.IINC == opcode) {
                isIncrement = true;
                count = str.readUnsignedShort();
            } else {
                isIncrement = false;
            }
        }
    }

    public static class BreakPoint extends Instruction {
    }

    public static class ImpDep extends Instruction {
        public boolean second;

        public ImpDep(boolean second) {
            this.second = second;
        }
    }

    public enum Type {
        INTEGER, LONG, FLOAT, DOUBLE, OBJECT, BYTE, CHAR, SHORT, NONE
    }

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER, NEGATE, SHIFT_LEFT, SHIFT_RIGHT, LOGICAL_SHIFT_RIGHT, BITWISE_OR, BITWISE_XOR, BITWISE_AND
    }

    public enum Condition {
        EQUAL, NOT_EQUAL, GREATER_THEN, GREATER_THEN_OR_EQUAL, LESS_THEN, LESS_THEN_OR_EQUAL, NULL, NOT_NULL
    }

    public enum InvokeType {
        VIRTUAL, SPECIAL, STATIC, INTERFACE
    }

    public enum Opcode {
        NOP(0), ACONST_NULL(1), ICONST_M1(2),
        ICONST_0(3), ICONST_1(4), ICONST_2(5), ICONST_3(6), ICONST_4(7), ICONST_5(8), LCONST_0(9), LCONST_1(0xA), FCONST_0(0xB), FCONST_1(0xC), FCONST_2(0xD), DCONST_0(0xE), DCONST_1(0xF),
        BIPUSH(0x10), SIPUSH(0x11), LDC(0x12), LDC_W(0x13), LDC_2_W(0x14),
        ILOAD(0x15), LLOAD(0x16), FLOAD(0x17), DLOAD(0x18), ALOAD(0x19), ILOAD_0(0x1A), ILOAD_1(0x1B), ILOAD_2(0x1C), ILOAD_3(0x1D), LLOAD_0(0x1E), LLOAD_1(0x1F), LLOAD_2(0x20), LLOAD_3(0x21),
        FLOAD_0(0x22), FLOAD_1(0x23), FLOAD_2(0x24), FLOAD_3(0x25), DLOAD_0(0x26), DLOAD_1(0x27), DLOAD_2(0x28), DLOAD_3(0x29), ALOAD_0(0x2A), ALOAD_1(0x2B), ALOAD_2(0x2C), ALOAD_3(0x2D),
        IALOAD(0x2E), LALOAD(0x2F), FALOAD(0x30), DALOAD(0x31), AALOAD(0x32), BALOAD(0x33), CALOAD(0x34), SALOAD(0x35),
        ISTORE(0x36), LSTORE(0x37), FSTORE(0x38), DSTORE(0x39), ASTORE(0x3A), ISTORE_0(0x3B), ISTORE_1(0x3C), ISTORE_2(0x3D), ISTORE_3(0x3E), LSTORE_0(0x3F), LSTORE_1(0x40), LSTORE_2(0x41), LSTORE_3(0x42),
        FSTORE_0(0x43), FSTORE_1(0x44), FSTORE_2(0x45), FSTORE_3(0x46), DSTORE_0(0x47), DSTORE_1(0x48), DSTORE_2(0x49), DSTORE_3(0x4A), ASTORE_0(0x4B), ASTORE_1(0x4C), ASTORE_2(0x4D), ASTORE_3(0x4E),
        IASTORE(0x4F), LASTORE(0x50), FASTORE(0x51), DASTORE(0x52), AASTORE(0x53), BASTORE(0x54), CASTORE(0x55), SASTORE(0x56),
        POP(0x57), POP2(0x58), DUP(0x59), DUP_X1(0x5A), DUP_X2(0x5B), DUP2(0x5C), DUP2_X1(0x5D), DUP2_X2(0x5E), SWAP(0x5F),
        IADD(0x60), LADD(0x61), FADD(0x62), DADD(0x63), ISUB(0x64), LSUB(0x65), FSUB(0x66), DSUB(0x67), IMUL(0x68), LMUL(0x69), FMUL(0x6A), DMUL(0x6B), IDIV(0x6C), LDIV(0x6D), FDIV(0x6E), DDIV(0x6F), IREM(0x70), LREM(0x71), FREM(0x72), DREM(0x73),
        INEG(0x74), LNEG(0x75), FNEG(0x76), DNEG(0x77), ISHL(0x78), LSHL(0x79), ISHR(0x7A), LSHR(0x7B), IUSHR(0x7C), LUSHR(0x7D), IAND(0x7E), LAND(0x7F), IOR(0x80), LOR(0x81), IXOR(0x82), LXOR(0x83), IINC(0x84),
        I2L(0x85), I2F(0x86), I2D(0x87), L2I(0x88), L2F(0x89), L2D(0x8A), F2I(0x8B), F2L(0x8C), F2D(0x8D), D2I(0x8E), D2L(0x8F), D2F(0x90), I2B(0x91), I2C(0x92), I2S(0x93),
        LCMP(0x94), FCMPL(0x95), FCMPG(0x96), DCMPL(0x97), DCMPG(0x98),
        IFEQ(0x99), IFNE(0x9A), IFLT(0x9B), IFGE(0x9C), IFGT(0x9D), IFLE(0x9E), IF_ICMPEQ(0x9F), IF_ICMPNE(0xA0), IF_ICMPLT(0xA1), IF_ICMPGE(0xA2), IF_ICMPGT(0xA3), IF_ICMPLE(0xA4), IF_ACMPEQ(0xA5), IF_ACMPNE(0xA6),
        GOTO(0xA7), JSR(0xA8), RET(0xA9), TABLESWITCH(0xAA), LOOKUPSWITCH(0xAB), IRETURN(0xAC), LRETURN(0xAD), FRETURN(0xAE), DRETURN(0xAF), ARETURN(0xB0), RETURN(0xB1),
        GETSTATIC(0xB2), PUTSTATIC(0xB3), GETFIELD(0xB4), PUTFIELD(0xB5), INVOKEVIRTUAL(0xB6), INVOKESPECIAL(0xB7), INVOKESTATIC(0xB8), INVOKEINTERFACE(0xB9), INVOKEDYNAMIC(0xBA),
        NEW(0xBB), NEWARRAY(0xBC), ANEWARRAY(0xBD), ARRAYLENGTH(0xBE), ATHROW(0xBF), CHECKCAST(0xC0), INSTANCEOF(0xC1), MONITORENTER(0xC2), MONITOREXIT(0xC3), WIDE(0xC4), MULTIANEWARRAY(0xC5),
        IFNULL(0xC6), IFNONNULL(0xC7), GOTO_W(0xC8), JSR_W(0xC9), BREAKPOINT(0xCA), IMPDEP1(0xFE), IMPDEP2(0xFF);

        public final int value;

        Opcode(int i) {
            value = i;
        }

        public static Opcode get(int value) {
            for (Opcode opcode : Opcode.values()) {
                if (opcode.value == value)
                    return opcode;
            }
            return Opcode.NOP;
        }

        public void write(DataOutputStream out) throws IOException {
            out.writeByte(value);
        }
    }
}

