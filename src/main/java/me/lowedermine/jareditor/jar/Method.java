package me.lowedermine.jareditor.jar;

import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.TypeAnnotation;
import me.lowedermine.jareditor.jar.annotations.ElementValuePair;
import me.lowedermine.jareditor.jar.code.instruction.Instruction;
import me.lowedermine.jareditor.jar.code.stackmapframe.StackMapFrame;
import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;
import me.lowedermine.jareditor.jar.descriptors.IDescriptor;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.MethodInfo;
import me.lowedermine.jareditor.jar.signatures.ClassSignature;
import me.lowedermine.jareditor.jar.signatures.MethodSignature;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Method {
    public MethodInfoSegment info = new MethodInfoSegment();
    public CodeSegment code = null;
    public AnnotationSegment annotation = new AnnotationSegment();

    public Method(Method copy) {
        info = copy.info;
        code = copy.code;
        annotation = copy.annotation;
    }

    public Method(DataInputStream in, ConstantPool cp) throws IOException {
        info.accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.METHOD);
        String name = (String) cp.ro(in.readUnsignedShort());
        String desc = (String) cp.ro(in.readUnsignedShort());
        info.info = new MethodInfo(name, (MethodDescriptor) IDescriptor.parse(desc));
        int attributeLength = in.readUnsignedShort();
        for (int i = 0; i < attributeLength; i++) parseAttribute(in, cp);
        recalculateSegments();
    }
    private void parseAttribute(DataInputStream in, ConstantPool cp) throws IOException {
        String name = (String) cp.ro(in.readUnsignedShort());
        in.readInt();
        switch (name) {
            case "Synthetic":
                info.synthetic = true;
                break;
            case "Deprecated":
                info.deprecated = true;
                break;
            case "Signature":
                info.signature = new MethodSignature((String) cp.ro(in.readUnsignedShort()));
                break;
            case "AnnotationDefault":
                info.annotationDefault = new ElementValuePair.ElementValue(in, cp);
                break;
            case "Exceptions":
                info.exceptions = new ClassInfo[in.readUnsignedShort()];
                for (int i = 0; i < info.exceptions.length; i++) info.exceptions[i] = (ClassInfo) cp.ro(in.readUnsignedShort());
                break;
            case "MethodParameters":
                info.methodParameters = new MethodParameter[in.readUnsignedByte()];
                for (int i = 0; i < info.methodParameters.length; i++) info.methodParameters[i] = new MethodParameter(in, cp);
                break;
            case "Code":
                code = code == null ? new CodeSegment() : code;
                code.maxStack = in.readUnsignedShort();
                code.maxLocals = in.readUnsignedShort();
                int offset = 0;
                byte[] rawCode = new byte[in.readInt()];
                if (in.read(rawCode) != rawCode.length) throw new IOException("Buffer is not large enough");
                List<Integer> indexList = new ArrayList<>();
                List<Instruction> listInstructions = new ArrayList<>();
                DataInputStream i1 = new DataInputStream(new ByteArrayInputStream(rawCode));
                DataInputStream i2 = new DataInputStream(new ByteArrayInputStream(rawCode));
                while (i1.available() != 0) {
                    listInstructions.add(Instruction.parse(i1, cp, offset));
                    indexList.add(offset);
                    offset += Instruction.parseLengthRaw(i2, offset);
                }
                i1.close();
                i2.close();
                indexList.add(offset);
                int[][] indexMap = new int[2][];
                indexMap[0] = new int[Collections.max(indexList) + 1];
                indexMap[1] = new int[indexList.size()];
                for (int i = 0; i < indexList.size(); i++) {
                    indexMap[0][indexList.get(i)] = i;
                    indexMap[1][i] = indexList.get(i);
                }
                code.instructions = listInstructions.toArray(new Instruction[0]);
                for (int i = 0; i < code.instructions.length; i++) {
                    if (code.instructions[i] instanceof Instruction.Jump) {
                        Instruction.Jump instruction = (Instruction.Jump) code.instructions[i];
                        instruction.offset = indexMap[0][indexMap[1][i] + instruction.offset];
                    } else if (code.instructions[i] instanceof Instruction.TableSwitch) {
                        Instruction.TableSwitch instruction = (Instruction.TableSwitch) code.instructions[i];
                        instruction.defaultOffset = indexMap[0][indexMap[1][i] + instruction.defaultOffset];
                        for (int i3 = 0; i3 < instruction.caseOffsets.length; i3++) {
                            instruction.caseOffsets[i3] = indexMap[0][indexMap[1][i] + instruction.caseOffsets[i3]];
                        }
                    } else if (code.instructions[i] instanceof Instruction.LookupSwitch) {
                        Instruction.LookupSwitch instruction = (Instruction.LookupSwitch) code.instructions[i];
                        instruction.defaultOffset = indexMap[0][indexMap[1][i] + instruction.defaultOffset];
                        for (Instruction.LookupSwitch.Pair pair : instruction.pairs)
                            pair.offset = indexMap[0][indexMap[1][i] + pair.offset];
                    }
                }
                code.exceptionHandlers = new ExceptionHandler[in.readUnsignedShort()];
                for (int i = 0; i < code.exceptionHandlers.length; i++) code.exceptionHandlers[i] = new ExceptionHandler(in, cp, indexMap[0]);
                int attributeLength = in.readUnsignedShort();
                for (int i = 0; i < attributeLength; i++) parseCodeAttribute(in, cp, indexMap);
                break;
            case "RuntimeVisibleParameterAnnotations":
                annotation.visibleParameter = new Annotation[in.readUnsignedShort()][];
                for (int i = 0; i < annotation.visibleParameter.length; i++) {
                    annotation.visibleParameter[i] = new Annotation[in.readUnsignedShort()];
                    for (int j = 0; j < annotation.visibleParameter[i].length; j++) {
                        annotation.visibleParameter[i][j] = new Annotation(in, cp);
                    }
                }
                break;
            case "RuntimeInvisibleParameterAnnotations":
                annotation.invisibleParameter = new Annotation[in.readUnsignedShort()][];
                for (int i = 0; i < annotation.invisibleParameter.length; i++) {
                    annotation.invisibleParameter[i] = new Annotation[in.readUnsignedShort()];
                    for (int j = 0; j < annotation.invisibleParameter[i].length; j++) {
                        annotation.invisibleParameter[i][j] = new Annotation(in, cp);
                    }
                }
                break;
            case "RuntimeVisibleAnnotations":
                annotation.visible = new Annotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.visible.length; i++) annotation.visible[i] = new Annotation(in, cp);
                break;
            case "RuntimeInvisibleAnnotations":
                annotation.invisible = new Annotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.invisible.length; i++) annotation.invisible[i] = new Annotation(in, cp);
                break;
            case "RuntimeVisibleTypeAnnotations":
                annotation.visibleType = new TypeAnnotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.visibleType.length; i++) annotation.visibleType[i] = new TypeAnnotation(in, cp);
                break;
            case "RuntimeInvisibleTypeAnnotations":
                annotation.invisibleType = new TypeAnnotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.invisibleType.length; i++) annotation.invisibleType[i] = new TypeAnnotation(in, cp);
                break;
        }
    }

    private void parseCodeAttribute(DataInputStream in, ConstantPool cp, int[][] indexMap) throws IOException {
        String name = (String) cp.ro(in.readUnsignedShort());
        in.readInt();
        switch (name) {
            case "LineNumberTable":
                code.lineNumberTable = new LineNumber[in.readUnsignedShort()];
                for (int i = 0; i < code.lineNumberTable.length; i++) code.lineNumberTable[i] = new LineNumber(in, indexMap[0]);
                break;
            case "LocalVariableTable":
                code.localVariableTable = new LocalVariable[in.readUnsignedShort()];
                for (int i = 0; i < code.localVariableTable.length; i++) code.localVariableTable[i] = new LocalVariable(in, cp, indexMap[0]);
                break;
            case "LocalVariableTypeTable":
                code.localVariableTypeTable = new LocalVariableType[in.readUnsignedShort()];
                for (int i = 0; i < code.localVariableTypeTable.length; i++) code.localVariableTypeTable[i] = new LocalVariableType(in, cp, indexMap[0]);
                break;
            case "StackMapTable":
                code.stackMapTable = new StackMapFrame[in.readUnsignedShort()];
                int offset = 0;
                for (int i = 0; i < code.stackMapTable.length; i++) {
                    code.stackMapTable[i] = StackMapFrame.parse(in, cp, offset, indexMap[0]);
                    offset = indexMap[1][code.stackMapTable[i].offset] + 1;
                }
                break;
            case "RuntimeVisibleTypeAnnotations":
                code.annotation = code.annotation == null ? new CodeSegment.AnnotationSegment() : code.annotation;
                code.annotation.visible = new TypeAnnotation[in.readUnsignedShort()];
                for (int i = 0; i < code.annotation.visible.length; i++) code.annotation.visible[i] = new TypeAnnotation(in, cp);
                break;
            case "RuntimeInvisibleTypeAnnotations":
                code.annotation = code.annotation == null ? new CodeSegment.AnnotationSegment() : code.annotation;
                code.annotation.invisible = new TypeAnnotation[in.readUnsignedShort()];
                for (int i = 0; i < code.annotation.invisible.length; i++) code.annotation.invisible[i] = new TypeAnnotation(in, cp);
                break;
        }
    }

    public void toPool(ConstantPoolBuilder cp, List<ClassFile.BootstrapMethod> methods) {
        {
            cp.add(info.info.name);
            cp.add(info.info.desc.toRaw());
            if (info.deprecated) {
                cp.add("Deprecated");
            }
            if (info.synthetic) {
                cp.add("Synthetic");
            }
            if (info.signature != null) {
                cp.add("Signature");
                cp.add(info.signature.toRaw());
            }
            if (info.exceptions != null) {
                cp.add("Exceptions");
                cp.addAll(info.exceptions);
            }
            if (info.methodParameters != null) {
                cp.add("MethodParameters");
                for (MethodParameter methodParameter : info.methodParameters) methodParameter.toPool(cp);
            }
            if (info.annotationDefault != null) {
                cp.add("AnnotationDefault");
                info.annotationDefault.toPool(cp);
            }
        }
        if (code != null) {
            cp.add("Code");
            for (Instruction instruction : code.instructions) {
                instruction.toPool(cp);
                if (instruction instanceof Instruction.InvokeDynamic)
                    ((Instruction.InvokeDynamic) instruction).toPool(cp, methods);
                if (instruction instanceof Instruction.LoadConst)
                    ((Instruction.LoadConst) instruction).toPool(cp, methods);
            }
            if (code.exceptionHandlers != null) {
                for (ExceptionHandler handler : code.exceptionHandlers) {
                    handler.toPool(cp);
                }
            }
            if (code.localVariableTable != null) {
                cp.add("LocalVariableTable");
                for (LocalVariable localVariable : code.localVariableTable) localVariable.toPool(cp);
            }
            if (code.localVariableTypeTable != null) {
                cp.add("LocalVariableTypeTable");
                for (LocalVariableType localVariable : code.localVariableTypeTable) localVariable.toPool(cp);
            }
            if (code.lineNumberTable != null) {
                cp.add("LineNumberTable");
            }
            if (code.stackMapTable != null) {
                cp.add("StackMapTable");
                for (StackMapFrame stackMapFrame : code.stackMapTable) stackMapFrame.toPool(cp);
            }
            if (code.annotation != null) {
                if (code.annotation.visible != null) {
                    cp.add("RuntimeVisibleTypeAnnotations");
                    for (TypeAnnotation annotation1 : code.annotation.visible) annotation1.toPool(cp);
                }
                if (code.annotation.invisible != null) {
                    cp.add("RuntimeInvisibleTypeAnnotations");
                    for (TypeAnnotation annotation1 : code.annotation.invisible) annotation1.toPool(cp);
                }
            }
        }
        if (annotation != null) {
            if (annotation.visible != null) {
                cp.add("RuntimeVisibleAnnotations");
                for (Annotation annotation1 : annotation.visible) annotation1.toPool(cp);
            }
            if (annotation.invisible != null) {
                cp.add("RuntimeInvisibleAnnotations");
                for (Annotation annotation1 : annotation.invisible) annotation1.toPool(cp);
            }
            if (annotation.visibleParameter != null) {
                cp.add("RuntimeVisibleParameterAnnotations");
                for (Annotation[] annotations : annotation.visibleParameter) for (Annotation annotation1 : annotations) annotation1.toPool(cp);
            }
            if (annotation.invisibleParameter != null) {
                cp.add("RuntimeInvisibleParameterAnnotations");
                for (Annotation[] annotations : annotation.invisibleParameter) for (Annotation annotation1 : annotations) annotation1.toPool(cp);
            }
            if (annotation.visibleType != null) {
                cp.add("RuntimeVisibleTypeAnnotations");
                for (TypeAnnotation annotation1 : annotation.visibleType) annotation1.toPool(cp);
            }
            if (annotation.invisibleType != null) {
                cp.add("RuntimeInvisibleTypeAnnotations");
                for (TypeAnnotation annotation1 : annotation.invisibleType) annotation1.toPool(cp);
            }
        }
    }

    public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
        out.writeShort(AccessFlags.toCode(info.accessFlags));
        out.writeShort(cp.indexOf(info.info.name));
        out.writeShort(cp.indexOf(info.info.desc.toRaw()));
        int attributes = 0;
        {
            if (info.synthetic)
                attributes++;
            if (info.deprecated)
                attributes++;
            if (info.annotationDefault != null)
                attributes++;
            if (info.signature != null)
                attributes++;
            if (info.methodParameters != null)
                attributes++;
            if (info.exceptions != null)
                attributes++;
        }
        if (code != null) {
            attributes++;
        }
        if (annotation != null) {
            if (annotation.visible != null)
                attributes++;
            if (annotation.invisible != null)
                attributes++;
            if (annotation.visibleParameter != null)
                attributes++;
            if (annotation.invisibleParameter != null)
                attributes++;
            if (annotation.visibleType != null)
                attributes++;
            if (annotation.invisibleType != null)
                attributes++;
        }
        out.writeShort(attributes);
        {
            if (info.synthetic) {
                out.writeShort(cp.indexOf("Synthetic"));
                out.writeInt(0);
            }
            if (info.deprecated) {
                out.writeShort(cp.indexOf("Deprecated"));
                out.writeInt(0);
            }
            if (info.annotationDefault != null) {
                out.writeShort(cp.indexOf("AnnotationDefault"));
                out.writeInt(info.annotationDefault.toLength());
                info.annotationDefault.toStream(out, cp);
            }
            if (info.signature != null) {
                out.writeShort(cp.indexOf("Signature"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(info.signature.toRaw()));
            }
            if (info.methodParameters != null) {
                out.writeShort(cp.indexOf("MethodParameters"));
                out.writeInt(1 + (info.methodParameters.length * 4));
                out.writeByte(info.methodParameters.length);
                for (MethodParameter methodParameter : info.methodParameters) methodParameter.toStream(out, cp);
            }
            if (info.exceptions != null) {
                out.writeShort(cp.indexOf("Exceptions"));
                out.writeInt(2 + (info.exceptions.length * 2));
                out.writeShort(info.exceptions.length);
                for (ClassInfo exception : info.exceptions) out.writeShort(cp.indexOf(exception));
            }
        }
        if (code != null) {
            out.writeShort(cp.indexOf("Code"));
            out.writeInt(codeToLength(cp));
            codeToStream(out, cp);
        }
        if (annotation != null) {
            if (annotation.visible != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleAnnotations"));
                int length = 2;
                for (Annotation annotation1 : annotation.visible) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.visible.length);
                for (Annotation annotation1 : annotation.visible) annotation1.toStream(out, cp);
            }
            if (annotation.invisible != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleAnnotations"));
                int length = 2;
                for (Annotation annotation1 : annotation.invisible) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.invisible.length);
                for (Annotation annotation1 : annotation.invisible) annotation1.toStream(out, cp);
            }
            if (annotation.visibleParameter != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleParameterAnnotations"));
                int length = 2;
                for (Annotation[] annotations1 : annotation.visibleParameter) {
                    length += 2;
                    for (Annotation annotation1 : annotations1) length += annotation1.toLength();
                }
                out.writeInt(length);
                out.writeShort(annotation.visibleParameter.length);
                for (Annotation[] annotations1 : annotation.visibleParameter) {
                    out.writeShort(annotations1.length);
                    for (Annotation annotation1 : annotations1) annotation1.toStream(out, cp);
                }
            }
            if (annotation.invisibleParameter != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleParameterAnnotations"));
                int length = 2;
                for (Annotation[] annotations1 : annotation.invisibleParameter) {
                    length += 2;
                    for (Annotation annotation1 : annotations1) length += annotation1.toLength();
                }
                out.writeInt(length);
                out.writeShort(annotation.invisibleParameter.length);
                for (Annotation[] annotations1 : annotation.invisibleParameter) {
                    out.writeShort(annotations1.length);
                    for (Annotation annotation1 : annotations1) annotation1.toStream(out, cp);
                }
            }
            if (annotation.visibleType != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleTypeAnnotations"));
                int length = 2;
                for (TypeAnnotation annotation1 : annotation.visibleType) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.visibleType.length);
                for (TypeAnnotation annotation1 : annotation.visibleType) annotation1.toStream(out, cp);
            }
            if (annotation.invisibleType != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleTypeAnnotations"));
                int length = 2;
                for (TypeAnnotation annotation1 : annotation.invisibleType) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.invisibleType.length);
                for (TypeAnnotation annotation1 : annotation.invisibleType) annotation1.toStream(out, cp);
            }
        }
    }

    public void codeToStream(DataOutputStream out, ConstantPool cp) throws IOException {
        out.writeShort(code.maxStack);
        out.writeShort(code.maxLocals);
        List<Integer> indexList = new ArrayList<>();
        int length = 0;
        for (Instruction instruction : code.instructions) {
            indexList.add(length);
            length += instruction.toLength(cp);
        }
        indexList.add(length);
        int[][] indexMap = new int[2][];
        indexMap[0] = new int[Collections.max(indexList) + 1];
        indexMap[1] = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            indexMap[0][indexList.get(i)] = i;
            indexMap[1][i] = indexList.get(i);
        }
        for (int i = 0; i < code.instructions.length; i++) {
            if (code.instructions[i] instanceof Instruction.Jump) {
                Instruction.Jump instruction = (Instruction.Jump) code.instructions[i];
                instruction.offset = indexMap[1][instruction.offset] - indexMap[1][i];
            } else if (code.instructions[i] instanceof Instruction.TableSwitch) {
                Instruction.TableSwitch instruction = (Instruction.TableSwitch) code.instructions[i];
                instruction.defaultOffset = indexMap[1][instruction.defaultOffset] - indexMap[1][i];
                for (int i3 = 0; i3 < instruction.caseOffsets.length; i3++)
                    instruction.caseOffsets[i3] = indexMap[1][instruction.caseOffsets[i3]] - indexMap[1][i];
            } else if (code.instructions[i] instanceof Instruction.LookupSwitch) {
                Instruction.LookupSwitch instruction = (Instruction.LookupSwitch) code.instructions[i];
                instruction.defaultOffset = indexMap[1][instruction.defaultOffset] - indexMap[1][i];
                for (Instruction.LookupSwitch.Pair pair : instruction.pairs)
                    pair.offset = indexMap[1][pair.offset] - indexMap[1][i];
            }
        }
        out.writeInt(length);
        for (Instruction instruction : code.instructions) instruction.toStream(out, cp);
        if (code.exceptionHandlers != null) {
            out.writeShort(code.exceptionHandlers.length);
            for (ExceptionHandler exceptionHandler : code.exceptionHandlers)
                exceptionHandler.toStream(out, cp, indexMap[1]);
        } else {
            out.writeShort(0);
        }
        int attributes = 0;
        {
            if (code.lineNumberTable != null) {
                attributes++;
            }
            if (code.localVariableTable != null) {
                attributes++;
            }
            if (code.localVariableTypeTable != null) {
                attributes++;
            }
            if (code.stackMapTable != null) {
                attributes++;
            }
        }
        if (code.annotation != null) {
            if (code.annotation.visible != null) {
                attributes++;
            }
            if (code.annotation.invisible != null) {
                attributes++;
            }
        }
        out.writeShort(attributes);
        {
            if (code.lineNumberTable != null) {
                out.writeShort(cp.indexOf("LineNumberTable"));
                out.writeInt(2 + (code.lineNumberTable.length * 4));
                out.writeShort(code.lineNumberTable.length);
                for (LineNumber lineNumber : code.lineNumberTable) lineNumber.toStream(out, indexMap[1]);
            }
            if (code.localVariableTable != null) {
                out.writeShort(cp.indexOf("LocalVariableTable"));
                out.writeInt(2 + (code.localVariableTable.length * 10));
                out.writeShort(code.localVariableTable.length);
                for (LocalVariable localVariable : code.localVariableTable) localVariable.toStream(out, cp, indexMap[1]);
            }
            if (code.localVariableTypeTable != null) {
                out.writeShort(cp.indexOf("LocalVariableTypeTable"));
                out.writeInt(2 + (code.localVariableTypeTable.length * 10));
                out.writeShort(code.localVariableTypeTable.length);
                for (LocalVariableType localVariableType : code.localVariableTypeTable) localVariableType.toStream(out, cp, indexMap[1]);
            }
            if (code.stackMapTable != null) {
                out.writeShort(cp.indexOf("StackMapTable"));
                int subLength = 2;
                for (StackMapFrame stackMapFrame : code.stackMapTable) subLength += stackMapFrame.toLength();
                out.writeInt(subLength);
                out.writeShort(code.stackMapTable.length);
                int prevOffset = 0;
                for (StackMapFrame stackMapFrame : code.stackMapTable) {
                    stackMapFrame.toStream(out, cp, prevOffset, indexMap[1]);
                    prevOffset = indexMap[1][stackMapFrame.offset] + 1;
                }
            }
        }
        if (code.annotation != null) {
            if (code.annotation.visible != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleTypeAnnotations"));
                int subLength = 2;
                for (TypeAnnotation annotation1 : code.annotation.visible) subLength += annotation1.toLength();
                out.writeInt(subLength);
                out.writeShort(code.annotation.visible.length);
                for (TypeAnnotation annotation1 : code.annotation.visible) annotation1.toStream(out, cp);
            }
            if (code.annotation.invisible != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleTypeAnnotations"));
                int subLength = 2;
                for (TypeAnnotation annotation1 : code.annotation.invisible) subLength += annotation1.toLength();
                out.writeInt(subLength);
                out.writeShort(code.annotation.invisible.length);
                for (TypeAnnotation annotation1 : code.annotation.invisible) annotation1.toStream(out, cp);
            }
        }
    }

    public int codeToLength(ConstantPool cp) {
        int length = 12 + (code.exceptionHandlers == null ? 0 : code.exceptionHandlers.length * 8);
        for (Instruction instruction : code.instructions) {
            length += instruction.toLength(cp);
        }
        {
            if (code.lineNumberTable != null) {
                length += 8 + (code.lineNumberTable.length * 4);
            }
            if (code.localVariableTable != null) {
                length += 8 + (code.localVariableTable.length * 10);
            }
            if (code.localVariableTypeTable != null) {
                length += 8 + (code.localVariableTypeTable.length * 10);
            }
            if (code.stackMapTable != null) {
                length += 8;
                for (StackMapFrame stackMapFrame : code.stackMapTable) length += stackMapFrame.toLength();
            }
        }
        if (code.annotation != null) {
            if (code.annotation.visible != null) {
                length += 8;
                for (TypeAnnotation annotation1 : code.annotation.visible) length += annotation1.toLength();
            }
            if (code.annotation.invisible != null) {
                length += 8;
                for (TypeAnnotation annotation1 : code.annotation.invisible) length += annotation1.toLength();
            }
        }
        return length;
    }

    public void recalculateSegments() {
        if (code != null) {
            if (code.exceptionHandlers != null && code.exceptionHandlers.length == 0)
                code.exceptionHandlers = null;
            if (code.stackMapTable != null && code.stackMapTable.length == 0)
                code.stackMapTable = null;
            if (code.lineNumberTable != null && code.lineNumberTable.length == 0)
                code.lineNumberTable = null;
            if (code.localVariableTable != null && code.localVariableTable.length == 0)
                code.localVariableTable = null;
            if (code.localVariableTypeTable != null && code.localVariableTypeTable.length == 0)
                code.localVariableTypeTable = null;
            if (code.annotation != null) {
                if (code.annotation.visible != null && code.annotation.visible.length == 0)
                    code.annotation.visible = null;
                if (code.annotation.invisible != null && code.annotation.invisible.length == 0)
                    code.annotation.invisible = null;
                if (code.annotation.visible == null && code.annotation.invisible == null)
                    code.annotation = null;
            }
        }
        if (annotation != null) {
            if (annotation.visible != null && annotation.visible.length == 0)
                annotation.visible = null;
            if (annotation.invisible != null && annotation.invisible.length == 0)
                annotation.invisible = null;
            if (annotation.visibleParameter != null && annotation.visibleParameter.length == 0)
                annotation.visibleParameter = null;
            if (annotation.invisibleParameter != null && annotation.invisibleParameter.length == 0)
                annotation.invisibleParameter = null;
            if (annotation.visibleType != null && annotation.visibleType.length == 0)
                annotation.visibleType = null;
            if (annotation.invisibleType != null && annotation.invisibleType.length == 0)
                annotation.invisibleType = null;
            if (annotation.visible == null && annotation.invisible == null && annotation.visibleType == null && annotation.invisibleType == null)
                annotation = null;
        }
    }

    public static class MethodInfoSegment {
        public AccessFlags[] accessFlags;
        public MethodInfo info;
        public ElementValuePair.ElementValue annotationDefault = null;
        public MethodSignature signature = null;
        public MethodParameter[] methodParameters = null;
        public ClassInfo[] exceptions = null;
        public boolean deprecated = false;
        public boolean synthetic = false;
    }

    public static class CodeSegment {
        public int maxLocals;
        public int maxStack;
        public Instruction[] instructions;
        public ExceptionHandler[] exceptionHandlers = null;
        public LocalVariable[] localVariableTable = null;
        public LocalVariableType[] localVariableTypeTable = null;
        public LineNumber[] lineNumberTable = null;
        public StackMapFrame[] stackMapTable = null;
        public AnnotationSegment annotation = null;

        public static class AnnotationSegment {
            public TypeAnnotation[] visible = null;
            public TypeAnnotation[] invisible = null;
        }
    }

    public static class AnnotationSegment {
        public Annotation[] visible = null;
        public Annotation[] invisible = null;
        public Annotation[][] visibleParameter = null;
        public Annotation[][] invisibleParameter = null;
        public TypeAnnotation[] visibleType = null;
        public TypeAnnotation[] invisibleType = null;
    }

    public static class ExceptionHandler {
        public int start;
        public int end;
        public int handler;
        public ClassInfo catchType;

        public ExceptionHandler(DataInputStream in, ConstantPool cp, int[] indexMap) throws IOException {
            start = indexMap[in.readUnsignedShort()];
            end = indexMap[in.readUnsignedShort()];
            handler = indexMap[in.readUnsignedShort()];
            catchType = (ClassInfo) cp.ro(in.readUnsignedShort());
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(catchType);
        }

        public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
            out.writeShort(indexMap[start]);
            out.writeShort(indexMap[end]);
            out.writeShort(indexMap[handler]);
            out.writeShort(cp.indexOf(catchType));
        }
    }

    public static class LineNumber {
        public int offset;
        public int number;

        public LineNumber(DataInputStream in, int[] indexMap) throws IOException {
            offset = indexMap[in.readUnsignedShort()];
            number = in.readUnsignedShort();
        }

        public void toStream(DataOutputStream out, int[] indexMap) throws IOException {
            out.writeShort(indexMap[offset]);
            out.writeShort(number);
        }
    }

    public static class LocalVariable {
        public int index;
        public int start;
        public int end;
        public String name;
        public FieldDescriptor desc;

        public LocalVariable(DataInputStream in, ConstantPool cp, int[] indexMap) throws IOException {
            int start = in.readUnsignedShort();
            this.start = indexMap[start];
            end = indexMap[start + in.readUnsignedShort()];
            name = (String) cp.ro(in.readUnsignedShort());
            desc = new FieldDescriptor((String) cp.ro(in.readUnsignedShort()));
            index = in.readUnsignedShort();
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(name);
            cp.add(desc.toRaw());
        }

        public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
            out.writeShort(indexMap[start]);
            out.writeShort(indexMap[end] - indexMap[start]);
            out.writeShort(cp.indexOf(name));
            out.writeShort(cp.indexOf(desc.toRaw()));
            out.writeShort(index);
        }
    }

    public static class LocalVariableType {
        public int index;
        public int start;
        public int end;
        public String name;
        public ClassSignature signature;

        public LocalVariableType(DataInputStream in, ConstantPool cp, int[] indexMap) throws IOException {
            int start = in.readUnsignedShort();
            this.start = indexMap[start];
            end = indexMap[start + in.readUnsignedShort()];
            name = (String) cp.ro(in.readUnsignedShort());
            signature = new ClassSignature((String) cp.ro(in.readUnsignedShort()));
            index = in.readUnsignedShort();
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(name);
            cp.add(signature.toRaw());
        }

        public void toStream(DataOutputStream out, ConstantPool cp, int[] indexMap) throws IOException {
            out.writeShort(indexMap[start]);
            out.writeShort(indexMap[end] - indexMap[start]);
            out.writeShort(cp.indexOf(name));
            out.writeShort(cp.indexOf(signature.toRaw()));
            out.writeShort(index);
        }

    }

    public static class MethodParameter {
        public String name;
        public AccessFlags[] accessFlags;

        public MethodParameter(DataInputStream in, ConstantPool cp) throws IOException {
            name = (String) cp.ro(in.readUnsignedShort());
            accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.METHOD_PARAMETER);
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(name);
        }

        public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
            out.writeShort(cp.indexOf(name));
            out.writeShort(AccessFlags.toCode(accessFlags));
        }
    }
}
