package me.lowedermine.jareditor.jar;

import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.AnnotationType;
import me.lowedermine.jareditor.jar.code.instruction.Instruction;
import me.lowedermine.jareditor.jar.constants.ConstantPool;
import me.lowedermine.jareditor.jar.constants.ConstantPoolBuilder;
import me.lowedermine.jareditor.jar.descriptors.DescriptorField;
import me.lowedermine.jareditor.jar.infos.*;
import me.lowedermine.jareditor.jar.signatures.SignatureClass;
import me.lowedermine.jareditor.utils.MyCloneable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassFile {
    public ClassInfoSegment info = new ClassInfoSegment();
    public MainDataSegment main = null;
    public NestedClassInfoSegment nested = null;
    public ModuleSegment module = null;
    public AnnotationSegment annotation = null;
    public DebugSegment debug = null;

    public ClassFile(InputStream rawIn) throws IOException {
        DataInputStream in = new DataInputStream(rawIn);
        if (in.readInt() != 0xCAFEBABE) throw new IOException("Class resolving error: checksum error");
        info.minorVersion = in.readUnsignedShort();
        info.majorVersion = in.readUnsignedShort();
        ConstantPool cp = new ConstantPool(in);
        info.accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.CLASS);
        info.name = (ClassInfo) cp.ro(in.readUnsignedShort());
        info.superName = (ClassInfo) cp.ro(in.readUnsignedShort());
        int interfacesCount = in.readUnsignedShort();
        if (interfacesCount > 0) {
            info.interfaces = new ClassInfo[interfacesCount];
            for (int i = 0; i < info.interfaces.length; i++)
                info.interfaces[i] = (ClassInfo) cp.ro(in.readUnsignedShort());
        }
        int fieldCount = in.readUnsignedShort();
        if (fieldCount > 0) {
            main = main == null ? new MainDataSegment() : main;
            main.fields = new Field[fieldCount];
            for (int i = 0; i < main.fields.length; i++) main.fields[i] = new Field(in, cp);
        }
        int methodCount = in.readUnsignedShort();
        if (methodCount > 0) {
            main = main == null ? new MainDataSegment() : main;
            main.methods = new Method[methodCount];
            for (int i = 0; i < main.methods.length; i++) main.methods[i] = new Method(in, cp);
        }
        BootstrapMethod[] bootstrapMethods = null;
        int attributesCount = in.readUnsignedShort();
        for (int i = 0; i < attributesCount; i++) {
            Object obj = parseAttribute(in, cp);
            if (obj instanceof BootstrapMethod[])
                bootstrapMethods = (BootstrapMethod[]) obj;
        }
        if (main != null)
            if (bootstrapMethods != null && main.methods != null)
                for (Method method : main.methods)
                    if (method.code != null)
                        for (int i = 0; i < method.code.instructions.length; i++)
                            if (method.code.instructions[i] instanceof Instruction.RawInvokeDynamic)
                                method.code.instructions[i] = new Instruction.InvokeDynamic((Instruction.RawInvokeDynamic) method.code.instructions[i], bootstrapMethods);
                            else if (method.code.instructions[i] instanceof Instruction.RawLoadConst)
                                method.code.instructions[i] = new Instruction.LoadConst((Instruction.RawLoadConst) method.code.instructions[i], bootstrapMethods);
    }

    private Object parseAttribute(DataInputStream in, ConstantPool cp) throws IOException {
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
                info.signature = new SignatureClass((String) cp.ro(in.readUnsignedShort()));
                break;
            case "BootstrapMethods":
                BootstrapMethod[] bootstrapMethods = new BootstrapMethod[in.readUnsignedShort()];
                for (int i = 0; i < bootstrapMethods.length; i++) bootstrapMethods[i] = new BootstrapMethod(in, cp);
                return bootstrapMethods;
            case "Record":
                main = main == null ? new MainDataSegment() : main;
                main.recordComponents = new RecordComponent[in.readUnsignedShort()];
                for (int i = 0; i < main.recordComponents.length; i++) main.recordComponents[i] = new RecordComponent(in, cp);
                break;
            case "InnerClasses":
                nested = nested == null ? new NestedClassInfoSegment() : nested;
                nested.innerClasses = new InnerClass[in.readUnsignedShort()];
                for (int i = 0; i < nested.innerClasses.length; i++) nested.innerClasses[i] = new InnerClass(in, cp);
                break;
            case "EnclosingMethod":
                nested = nested == null ? new NestedClassInfoSegment() : nested;
                ClassInfo clazz = (ClassInfo) cp.ro(in.readUnsignedShort());
                MethodInfo desc = (MethodInfo) cp.ro(in.readUnsignedShort());
                nested.enclosingMethod = new ClassMethodInfo(clazz, desc, false);
                break;
            case "NestHost":
                nested = nested == null ? new NestedClassInfoSegment() : nested;
                nested.nestHost = (ClassInfo) cp.ro(in.readUnsignedShort());
                break;
            case "NestMembers":
                nested = nested == null ? new NestedClassInfoSegment() : nested;
                nested.nestMembers = new ClassInfo[in.readUnsignedShort()];
                for (int i = 0; i < nested.nestMembers.length; i++) nested.nestMembers[i] = (ClassInfo) cp.ro(in.readUnsignedShort());
                break;
            case "PermittedSubclasses":
                nested = nested == null ? new NestedClassInfoSegment() : nested;
                nested.permittedSubclasses = new ClassInfo[in.readUnsignedShort()];
                for (int i = 0; i < nested.permittedSubclasses.length; i++) nested.permittedSubclasses[i] = (ClassInfo) cp.ro(in.readUnsignedShort());
                break;
            case "Module":
                module = module == null ? new ModuleSegment() : module;
                module.module = new Module(in, cp);
                break;
            case "ModuleMainClass":
                module = module == null ? new ModuleSegment() : module;
                module.mainClass = (ClassInfo) cp.ro(in.readUnsignedShort());
                break;
            case "ModulePackages":
                module = module == null ? new ModuleSegment() : module;
                module.packages = new PackageInfo[in.readUnsignedShort()];
                for (int i = 0; i < module.packages.length; i++) module.packages[i] = (PackageInfo) cp.ro(in.readUnsignedShort());
                break;
            case "RuntimeVisibleAnnotations":
                annotation = annotation == null ? new AnnotationSegment() : annotation;
                annotation.visible = new Annotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.visible.length; i++) annotation.visible[i] = new Annotation(in, cp);
                break;
            case "RuntimeInvisibleAnnotations":
                annotation = annotation == null ? new AnnotationSegment() : annotation;
                annotation.invisible = new Annotation[in.readUnsignedShort()];
                for (int i = 0; i < annotation.invisible.length; i++) annotation.invisible[i] = new Annotation(in, cp);
                break;
            case "RuntimeVisibleTypeAnnotations":
                annotation = annotation == null ? new AnnotationSegment() : annotation;
                annotation.visibleType = new AnnotationType[in.readUnsignedShort()];
                for (int i = 0; i < annotation.visibleType.length; i++) annotation.visibleType[i] = new AnnotationType(in, cp);
                break;
            case "RuntimeInvisibleTypeAnnotations":
                annotation = annotation == null ? new AnnotationSegment() : annotation;
                annotation.invisibleType = new AnnotationType[in.readUnsignedShort()];
                for (int i = 0; i < annotation.invisibleType.length; i++) annotation.invisibleType[i] = new AnnotationType(in, cp);
                break;
            case "SourceFile":
                debug = debug == null ? new DebugSegment() : debug;
                debug.sourceFile = (String) cp.ro(in.readUnsignedShort());
                break;
            case "SourceDebugExtension":
                debug = debug == null ? new DebugSegment() : debug;
                byte[] bytes = new byte[in.readUnsignedShort()];
                if (in.read(bytes) != bytes.length) throw new IOException("Buffer is not large enough");
                debug.sourceDebugExtension = new String(bytes, StandardCharsets.UTF_8);
                break;
        }
        return null;
    }

    public void toPool(ConstantPoolBuilder cp) {
        {
            cp.add(null);
            cp.add(info.name);
            cp.add(info.superName);
            if (info.interfaces != null)
                cp.addAll(info.interfaces);
            if (info.synthetic)
                cp.add("Synthetic");
            if (info.deprecated)
                cp.add("Deprecated");
            if (info.signature != null) {
                cp.add("Signature");
                cp.add(info.signature.toRaw());
            }
        }
        if (main != null) {
            if (main.fields != null)
                for (Field field : main.fields) field.toPool(cp);
            if (main.methods != null) {
                List<BootstrapMethod> methods = new ArrayList<>();
                for (Method method : main.methods) method.toPool(cp, methods);
            } if (main.recordComponents != null) {
                cp.add("Record");
                for (RecordComponent component : main.recordComponents) component.toPool(cp);
            }
        }
        if (nested != null) {
            if (nested.enclosingMethod != null) {
                cp.add("EnclosingMethod");
                cp.add(nested.enclosingMethod.clazz);
                cp.add(nested.enclosingMethod.getInfo());
            }
            if (nested.innerClasses != null) {
                cp.add("InnerClasses");
                for (InnerClass innerClass : nested.innerClasses) innerClass.toPool(cp);
            }
            if (nested.nestHost != null) {
                cp.add("NestHost");
                cp.add(nested.nestHost);
            }
            if (nested.nestMembers != null) {
                cp.add("NestMembers");
                cp.addAll(nested.nestMembers);
            }
            if (nested.permittedSubclasses != null) {
                cp.add("PermittedSubclasses");
                cp.addAll(nested.permittedSubclasses);
            }
        }
        if (module != null) {
            if (module.module != null) {
                cp.add("Module");
                module.module.toPool(cp);
            }
            if (module.mainClass != null) {
                cp.add("ModuleMainClass");
                cp.add(module.mainClass);
            }
            if (module.packages != null) {
                cp.add("ModulePackages");
                cp.addAll(module.packages);
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
            if (annotation.visibleType != null) {
                cp.add("RuntimeVisibleTypeAnnotations");
                for (AnnotationType annotation1 : annotation.visibleType) annotation1.toPool(cp);
            }
            if (annotation.invisibleType != null) {
                cp.add("RuntimeInvisibleTypeAnnotations");
                for (AnnotationType annotation1 : annotation.invisibleType) annotation1.toPool(cp);
            }
        }
        if (debug != null) {
            if (debug.sourceFile != null) {
                cp.add("SourceFile");
                cp.add(debug.sourceFile);
            }
            if (debug.sourceDebugExtension != null) {
                cp.add("SourceDebugExtension");
            }
        }
    }

    public void toStream(OutputStream stream) throws IOException {
        ConstantPoolBuilder cpb = new ConstantPoolBuilder();
        toPool(cpb);
        List<BootstrapMethod> bootstrapMethodList = new ArrayList<>();
        ConstantPool cp = cpb.build();
        DataOutputStream out = new DataOutputStream(stream);
        out.writeInt(0xCAFEBABE);
        out.writeShort(info.minorVersion);
        out.writeShort(info.majorVersion);
        cp.toBytes(out);
        out.writeShort(AccessFlags.toCode(info.accessFlags));
        out.writeShort(cp.indexOf(info.name));
        out.writeShort(cp.indexOf(info.superName));
        if (info.interfaces != null) {
            out.writeShort(info.interfaces.length);
            for (ClassInfo anInterface : info.interfaces) out.writeShort(cp.indexOf(anInterface));
        } else {
            out.writeShort(0);
        }
        if (main != null) {
            if (main.fields != null) {
                out.writeShort(main.fields.length);
                for (Field field : main.fields) field.toStream(out, cp);
            } else {
                out.writeShort(0);
            }
            if (main.methods != null) {
                out.writeShort(main.methods.length);
                for (Method method : main.methods) {
                    if (method.code != null)
                        for (int i = 0; i < method.code.instructions.length; i++)
                            if (method.code.instructions[i] instanceof Instruction.InvokeDynamic)
                                method.code.instructions[i] = ((Instruction.InvokeDynamic) method.code.instructions[i]).toRaw(bootstrapMethodList);
                            else if (method.code.instructions[i] instanceof Instruction.LoadConst)
                                method.code.instructions[i] = ((Instruction.LoadConst) method.code.instructions[i]).toRaw(bootstrapMethodList);
                    method.toStream(out, cp);
                }
            } else {
                out.writeShort(0);
            }
        } else {
            out.writeInt(0);
        }
        int attributes = 0;
        {
            if (info.synthetic) {
                attributes++;
            }
            if (info.deprecated) {
                attributes++;
            }
            if (info.signature != null) {
                attributes++;
            }
        }
        if (!bootstrapMethodList.isEmpty()) {
            attributes++;
        }
        if (main != null) {
            if (main.recordComponents != null) {
                attributes++;
            }
        }
        if (nested != null) {
            if (nested.enclosingMethod != null) {
                attributes++;
            }
            if (nested.innerClasses != null) {
                attributes++;
            }
            if (nested.nestHost != null) {
                attributes++;
            }
            if (nested.nestMembers != null) {
                attributes++;
            }
            if (nested.permittedSubclasses != null) {
                attributes++;
            }
        }
        if (module != null) {
            if (module.module != null) {
                attributes++;
            }
            if (module.mainClass != null) {
                attributes++;
            }
            if (module.packages != null) {
                attributes++;
            }
        }
        if (annotation != null) {
            if (annotation.visible != null) {
                attributes++;
            }
            if (annotation.invisible != null) {
                attributes++;
            }
            if (annotation.visibleType != null) {
                attributes++;
            }
            if (annotation.invisibleType != null) {
                attributes++;
            }
        }
        if (debug != null) {
            if (debug.sourceFile != null) {
                attributes++;
            }
            if (debug.sourceDebugExtension != null) {
                attributes++;
            }
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
            if (info.signature != null) {
                out.writeShort(cp.indexOf("Signature"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(info.signature.toRaw()));
            }
        }
        if (!bootstrapMethodList.isEmpty()) {
            out.writeShort(cp.indexOf("BootstrapMethods"));
            int length = 2;
            for (BootstrapMethod bootstrapMethod : bootstrapMethodList) length += bootstrapMethod.toLength();
            out.writeInt(length);
            out.writeShort(bootstrapMethodList.size());
            for (BootstrapMethod bootstrapMethod : bootstrapMethodList) bootstrapMethod.toStream(out, cp);
        }
        if (main != null) {
            if (main.recordComponents != null) {
                out.writeShort(cp.indexOf("Record"));
                int length = 2;
                for (RecordComponent recordComponent : main.recordComponents) length += recordComponent.toLength();
                out.writeInt(length);
                out.writeShort(main.recordComponents.length);
                for (RecordComponent recordComponent : main.recordComponents) recordComponent.toStream(out, cp);
            }
        }
        if (nested != null) {
            if (nested.enclosingMethod != null) {
                out.writeShort(cp.indexOf("EnclosingMethod"));
                out.writeInt(4);
                out.writeShort(cp.indexOf(nested.enclosingMethod.clazz));
                out.writeShort(cp.indexOf(nested.enclosingMethod.getInfo()));
            }
            if (nested.innerClasses != null) {
                out.writeShort(cp.indexOf("InnerClasses"));
                out.writeInt(2 + (nested.innerClasses.length * 8));
                out.writeShort(nested.innerClasses.length);
                for (InnerClass innerClass : nested.innerClasses) innerClass.toStream(out, cp);
            }
            if (nested.nestHost != null) {
                out.writeShort(cp.indexOf("NestHost"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(nested.nestHost));
            }
            if (nested.nestMembers != null) {
                out.writeShort(cp.indexOf("NestMembers"));
                out.writeInt(2 + (nested.nestMembers.length * 2));
                out.writeShort(nested.nestMembers.length);
                for (ClassInfo nestMember : nested.nestMembers) out.writeShort(cp.indexOf(nestMember));
            }
            if (nested.permittedSubclasses != null) {
                out.writeShort(cp.indexOf("PermittedSubclasses"));
                out.writeInt(2 + (nested.permittedSubclasses.length * 2));
                out.writeShort(nested.permittedSubclasses.length);
                for (ClassInfo permittedSubclass : nested.permittedSubclasses)
                    out.writeShort(cp.indexOf(permittedSubclass));
            }
        }
        if (module != null) {
            if (module.module != null) {
                out.writeShort(cp.indexOf("Module"));
                out.writeInt(module.module.toLength());
                module.module.toStream(out, cp);
            }
            if (module.mainClass != null) {
                out.writeShort(cp.indexOf("ModuleMainClass"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(module.mainClass));
            }
            if (module.packages != null) {
                out.writeShort(cp.indexOf("ModulePackages"));
                out.writeInt(2 + (module.packages.length * 2));
                out.writeShort(module.packages.length);
                for (PackageInfo aPackage : module.packages) out.writeShort(cp.indexOf(aPackage));
            }
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
            if (annotation.visibleType != null) {
                out.writeShort(cp.indexOf("RuntimeVisibleTypeAnnotations"));
                int length = 2;
                for (AnnotationType annotation1 : annotation.visibleType) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.visibleType.length);
                for (AnnotationType annotation1 : annotation.visibleType) annotation1.toStream(out, cp);
            }
            if (annotation.invisibleType != null) {
                out.writeShort(cp.indexOf("RuntimeInvisibleTypeAnnotations"));
                int length = 2;
                for (AnnotationType annotation1 : annotation.invisibleType) length += annotation1.toLength();
                out.writeInt(length);
                out.writeShort(annotation.invisibleType.length);
                for (AnnotationType annotation1 : annotation.invisibleType) annotation1.toStream(out, cp);
            }
        }
        if (debug != null) {
            if (debug.sourceFile != null) {
                out.writeShort(cp.indexOf("SourceFile"));
                out.writeInt(2);
                out.writeShort(cp.indexOf(debug.sourceFile));
            }
            if (debug.sourceDebugExtension != null) {
                out.writeShort(cp.indexOf("SourceDebugExtension"));
                out.writeInt(debug.sourceDebugExtension.getBytes(StandardCharsets.UTF_8).length);
                out.write(debug.sourceDebugExtension.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static class ClassInfoSegment {
        public int minorVersion;
        public int majorVersion;
        public AccessFlags[] accessFlags;
        public ClassInfo name;
        public ClassInfo superName;
        public ClassInfo[] interfaces = null;
        public SignatureClass signature = null;
        public boolean deprecated = false;
        public boolean synthetic = false;
    }

    public static class MainDataSegment {
        public Field[] fields = null;
        public Method[] methods = null;
        public RecordComponent[] recordComponents = null;
    }

    public static class NestedClassInfoSegment {
        public ClassMethodInfo enclosingMethod = null;
        public InnerClass[] innerClasses = null;
        public ClassInfo nestHost = null;
        public ClassInfo[] nestMembers = null;
        public ClassInfo[] permittedSubclasses = null;
    }

    public static class ModuleSegment {
        public Module module = null;
        public ClassInfo mainClass = null;
        public PackageInfo[] packages = null;
    }

    public static class AnnotationSegment {
        public Annotation[] visible = null;
        public Annotation[] invisible = null;
        public AnnotationType[] visibleType = null;
        public AnnotationType[] invisibleType = null;
    }

    public static class DebugSegment {
        public String sourceFile = null;
        public String sourceDebugExtension = null;
    }

    public static class BootstrapMethod extends MethodHandleInfo {

        public Object[] args;

        public BootstrapMethod(DataInputStream in, ConstantPool cp) throws IOException {
            MethodHandleInfo methodInfo = (MethodHandleInfo) cp.ro(in.readUnsignedShort());
            type = methodInfo.type;
            clazz = methodInfo.clazz;
            name = methodInfo.name;
            desc = methodInfo.desc;
            args = new Object[in.readUnsignedShort()];
            for (int i = 0; i < args.length; i++) args[i] = cp.ro(in.readUnsignedShort());
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(new MethodHandleInfo(type.to(), this));
            cp.addAll(args);
        }

        public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
            out.writeShort(cp.indexOf(new MethodHandleInfo(type.to(), this)));
            out.writeShort(args.length);
            for (Object arg : args) out.writeShort(cp.indexOf(arg));
        }

        public int toLength() {
            return 4 + (args.length * 2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BootstrapMethod that = (BootstrapMethod) o;
            return Arrays.equals(args, that.args);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }

        @Override
        public BootstrapMethod clone() {
            BootstrapMethod clone = (BootstrapMethod) super.clone();
            clone.args = args.clone();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof MyCloneable)
                    clone.args[i] = ((MyCloneable) args[i]).clone();
                else
                    clone.args[i] = args[i];
            }
            return clone;
        }
    }

    public static class InnerClass {
        public ClassInfo info;
        public ClassInfo outerInfo;
        public String originalName;
        public AccessFlags[] accessFlags;

        public InnerClass(DataInputStream in, ConstantPool cp) throws IOException {
            info = (ClassInfo) cp.ro(in.readUnsignedShort());
            outerInfo = (ClassInfo) cp.ro(in.readUnsignedShort());
            originalName = (String) cp.ro(in.readUnsignedShort());
            accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.INNERCLASS);
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
            cp.add(outerInfo);
            cp.add(originalName);
        }

        public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
            out.writeShort(cp.indexOf(info));
            out.writeShort(cp.indexOf(outerInfo));
            out.writeShort(cp.indexOf(originalName));
            out.writeShort(AccessFlags.toCode(accessFlags));
        }
    }

    public static class Module {
        public ModuleInfo info;
        public AccessFlags[] accessFlags;
        public String version;
        public Require[] requires;
        public Export[] exports;
        public Open[] opens;
        public ClassInfo[] uses;
        public Provide[] provides;

        public Module(DataInputStream in, ConstantPool cp) throws IOException {
            info = (ModuleInfo) cp.ro(in.readUnsignedShort());
            accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.MODULE);
            version = (String) cp.ro(in.readUnsignedShort());
            requires = new Require[in.readUnsignedShort()];
            for (int i = 0; i < requires.length; i++) requires[i] = new Require(in, cp);
            exports = new Export[in.readUnsignedShort()];
            for (int i = 0; i < exports.length; i++) exports[i] = new Export(in, cp);
            opens = new Open[in.readUnsignedShort()];
            for (int i = 0; i < opens.length; i++) opens[i] = new Open(in, cp);
            uses = new ClassInfo[in.readUnsignedShort()];
            for (int i = 0; i < uses.length; i++) uses[i] = (ClassInfo) cp.ro(in.readUnsignedShort());
            provides = new Provide[in.readUnsignedShort()];
            for (int i = 0; i < provides.length; i++) provides[i] = new Provide(in, cp);
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info);
            cp.add(version);
            for (Require require : requires) require.toPool(cp);
            for (Export export : exports) export.toPool(cp);
            for (Open open : opens) open.toPool(cp);
            cp.addAll(uses);
            for (Provide provide : provides) provide.toPool(cp);
        }

        public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
            out.writeShort(cp.indexOf(info));
            out.writeShort(AccessFlags.toCode(accessFlags));
            out.writeShort(cp.indexOf(version));
            out.writeShort(cp.indexOf(requires.length));
            for (Require require : requires) require.toStream(out, cp);
            out.writeShort(cp.indexOf(exports.length));
            for (Export export : exports) export.toStream(out, cp);
            out.writeShort(cp.indexOf(opens.length));
            for (Open open : opens) open.toStream(out, cp);
            out.writeShort(cp.indexOf(uses.length));
            for (ClassInfo use : uses) out.writeShort(cp.indexOf(use));
            out.writeShort(cp.indexOf(provides.length));
            for (Provide provide : provides) provide.toStream(out, cp);
        }

        public int toLength() {
            int length = 16 + (uses.length * 2) + (requires.length * 6);
            for (Export export : exports) length += export.toLength();
            for (Open open : opens) length += open.toLength();
            for (Provide provide : provides) length += provide.toLength();
            return length;
        }

        public static class Require {
            public ModuleInfo info;
            public AccessFlags[] accessFlags;
            public String version;

            public Require(DataInputStream in, ConstantPool cp) throws IOException {
                info = (ModuleInfo) cp.ro(in.readUnsignedShort());
                accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.MODULE_REQUIRE);
                version = (String) cp.ro(in.readUnsignedShort());
            }

            public void toPool(ConstantPoolBuilder cp) {
                cp.add(info);
                cp.add(version);
            }

            public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
                out.writeShort(cp.indexOf(info));
                out.writeShort(AccessFlags.toCode(accessFlags));
                out.writeShort(cp.indexOf(version));
            }
        }

        public static class Export {
            public PackageInfo info;
            public AccessFlags[] accessFlags;
            public ModuleInfo[] exports;

            public Export(DataInputStream in, ConstantPool cp) throws IOException {
                info = (PackageInfo) cp.ro(in.readUnsignedShort());
                accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.MODULE_EXPORT);
                exports = new ModuleInfo[in.readUnsignedShort()];
                for (int i = 0; i < exports.length; i++) exports[i] = (ModuleInfo) cp.ro(in.readUnsignedShort());
            }

            public void toPool(ConstantPoolBuilder cp) {
                cp.add(info);
                cp.addAll(exports);
            }

            public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
                out.writeShort(cp.indexOf(info));
                out.writeShort(AccessFlags.toCode(accessFlags));
                out.writeShort(exports.length);
                for (ModuleInfo export : exports) out.writeShort(cp.indexOf(export));
            }

            public int toLength() {
                return 6 + (exports.length * 2);
            }
        }

        public static class Open {
            public PackageInfo info;
            public AccessFlags[] accessFlags;
            public ModuleInfo[] opens;

            public Open(DataInputStream in, ConstantPool cp) throws IOException {
                info = (PackageInfo) cp.ro(in.readUnsignedShort());
                accessFlags = AccessFlags.fromCode(in.readUnsignedShort(), AccessFlags.Type.MODULE_OPEN);
                opens = new ModuleInfo[in.readUnsignedShort()];
                for (int i = 0; i < opens.length; i++) opens[i] = (ModuleInfo) cp.ro(in.readUnsignedShort());
            }

            public void toPool(ConstantPoolBuilder cp) {
                cp.add(info);
                cp.addAll(opens);
            }

            public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
                out.writeShort(cp.indexOf(info));
                out.writeShort(AccessFlags.toCode(accessFlags));
                out.writeShort(opens.length);
                for (ModuleInfo open : opens) out.writeShort(cp.indexOf(open));
            }

            public int toLength() {
                return 6 + (opens.length * 2);
            }
        }

        public static class Provide {
            public ClassInfo provided;
            public ClassInfo[] provides;

            public Provide(DataInputStream in, ConstantPool cp) throws IOException {
                provided = (ClassInfo) cp.ro(in.readUnsignedShort());
                provides = new ClassInfo[in.readUnsignedShort()];
                for (int i = 0; i < provides.length; i++) provides[i] = (ClassInfo) cp.ro(in.readUnsignedShort());
            }

            public void toPool(ConstantPoolBuilder cp) {
                cp.add(provided);
                cp.addAll(provides);
            }

            public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
                out.writeShort(cp.indexOf(provided));
                out.writeShort(provides.length);
                for (ClassInfo provide : provides) out.writeShort(cp.indexOf(provide));
            }

            public int toLength() {
                return 4 + (provides.length * 2);
            }
        }
    }

    public static class RecordComponent {

        public RecordInfoSegment info = new RecordInfoSegment();
        public AnnotationSegment annotation = null;

        public RecordComponent(DataInputStream in, ConstantPool cp) throws IOException {
            String name = (String) cp.ro(in.readUnsignedShort());
            String desc = (String) cp.ro(in.readUnsignedShort());
            info.info = new FieldInfo(name, new DescriptorField(desc));
            int attributeLength = in.readUnsignedShort();
            for (int i = 0; i < attributeLength; i++) parseAttribute(in, cp);
        }

        public void toPool(ConstantPoolBuilder cp) {
            cp.add(info.info.name);
            cp.add(info.info.desc.toRaw());
            if (info.signature != null)
                cp.add(info.signature.toRaw());
            if (annotation != null) {
                if (annotation.visible != null) {
                    cp.add("RuntimeVisibleAnnotations");
                    for (Annotation annotation1 : annotation.visible) annotation1.toPool(cp);
                }
                if (annotation.invisible != null) {
                    cp.add("RuntimeInvisibleAnnotations");
                    for (Annotation annotation1 : annotation.invisible) annotation1.toPool(cp);
                }
                if (annotation.visibleType != null) {
                    cp.add("RuntimeVisibleTypeAnnotations");
                    for (AnnotationType annotation1 : annotation.visibleType) annotation1.toPool(cp);
                }
                if (annotation.invisibleType != null) {
                    cp.add("RuntimeInvisibleTypeAnnotations");
                    for (AnnotationType annotation1 : annotation.invisibleType) annotation1.toPool(cp);
                }
            }
        }

        public void toStream(DataOutputStream out, ConstantPool cp) throws IOException {
            out.writeShort(cp.indexOf(info.info.name));
            out.writeShort(cp.indexOf(info.info.desc.toRaw()));
            int annotations = 0;
            if (annotation != null) {
                if (annotation.visible != null) {
                    annotations++;
                }
                if (annotation.invisible != null) {
                    annotations++;
                }
                if (annotation.visibleType != null) {
                    annotations++;
                }
                if (annotation.invisibleType != null) {
                    annotations++;
                }
            }
            out.writeShort(annotations);
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
                if (annotation.visibleType != null) {
                    out.writeShort(cp.indexOf("RuntimeVisibleTypeAnnotations"));
                    int length = 2;
                    for (AnnotationType annotation1 : annotation.visibleType) length += annotation1.toLength();
                    out.writeInt(length);
                    out.writeShort(annotation.visibleType.length);
                    for (AnnotationType annotation1 : annotation.visibleType) annotation1.toStream(out, cp);
                }
                if (annotation.invisibleType != null) {
                    out.writeShort(cp.indexOf("RuntimeInvisibleTypeAnnotations"));
                    int length = 2;
                    for (AnnotationType annotation1 : annotation.invisibleType) length += annotation1.toLength();
                    out.writeInt(length);
                    out.writeShort(annotation.invisibleType.length);
                    for (AnnotationType annotation1 : annotation.invisibleType) annotation1.toStream(out, cp);
                }
            }
        }

        public int toLength() {
            int length = 6;
            if (annotation != null) {
                if (annotation.visible != null) {
                    length += 6;
                    for (Annotation annotation1 : annotation.visible) length += annotation1.toLength();
                }
                if (annotation.invisible != null) {
                    length += 6;
                    for (Annotation annotation1 : annotation.invisible) length += annotation1.toLength();
                }
                if (annotation.visibleType != null) {
                    length += 6;
                    for (AnnotationType annotation1 : annotation.visibleType) length += annotation1.toLength();
                }
                if (annotation.invisibleType != null) {
                    length += 6;
                    for (AnnotationType annotation1 : annotation.invisibleType) length += annotation1.toLength();
                }
            }
            return length;
        }

        private void parseAttribute(DataInputStream in, ConstantPool cp) throws IOException {
            String name = (String) cp.ro(in.readUnsignedShort());
            in.readInt();
            switch (name) {
                case "Signature":
                    info.signature = new SignatureClass((String) cp.ro(in.readUnsignedShort()));
                    break;
                case "RuntimeVisibleAnnotations":
                    annotation = annotation == null ? new AnnotationSegment() : annotation;
                    annotation.visible = new Annotation[in.readUnsignedShort()];
                    for (int i = 0; i < annotation.visible.length; i++) annotation.visible[i] = new Annotation(in, cp);
                    break;
                case "RuntimeInvisibleAnnotations":
                    annotation = annotation == null ? new AnnotationSegment() : annotation;
                    annotation.invisible = new Annotation[in.readUnsignedShort()];
                    for (int i = 0; i < annotation.invisible.length; i++)
                        annotation.invisible[i] = new Annotation(in, cp);
                    break;
                case "RuntimeVisibleTypeAnnotations":
                    annotation = annotation == null ? new AnnotationSegment() : annotation;
                    annotation.visibleType = new AnnotationType[in.readUnsignedShort()];
                    for (int i = 0; i < annotation.visibleType.length; i++)
                        annotation.visibleType[i] = new AnnotationType(in, cp);
                    break;
                case "RuntimeInvisibleTypeAnnotations":
                    annotation = annotation == null ? new AnnotationSegment() : annotation;
                    annotation.invisibleType = new AnnotationType[in.readUnsignedShort()];
                    for (int i = 0; i < annotation.invisibleType.length; i++)
                        annotation.invisibleType[i] = new AnnotationType(in, cp);
                    break;
            }
        }

        public static class RecordInfoSegment {
            public FieldInfo info;
            public SignatureClass signature = null;
        }
    }
}
