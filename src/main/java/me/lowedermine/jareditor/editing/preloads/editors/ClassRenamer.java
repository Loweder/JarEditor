package me.lowedermine.jareditor.editing.preloads.editors;

import me.lowedermine.jareditor.editing.preloads.Renamer;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.Field;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.AnnotationType;
import me.lowedermine.jareditor.jar.annotations.ElementValuePair;
import me.lowedermine.jareditor.jar.code.instruction.Instruction;
import me.lowedermine.jareditor.jar.code.stackmapframe.*;
import me.lowedermine.jareditor.jar.descriptors.*;
import me.lowedermine.jareditor.jar.infos.*;
import me.lowedermine.jareditor.jar.signatures.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class ClassRenamer extends Renamer<ClassInfo, ClassInfo> {
    private Map<ClassInfo, ClassInfo> mappings = new HashMap<>();
    private Map<ClassInfo, ClassInfo> unMappings = new HashMap<>();

    @Override
    protected final void setMappings(Map<ClassInfo, ClassInfo> mappings) {
        this.mappings = mappings;
        if (mappings == null) {
            this.mappings = new HashMap<>();
        }
        unMappings = new HashMap<>();
        this.mappings.forEach((k, v) -> unMappings.put(v, k));
    }

    @Override
    public final ClassInfo map(ClassInfo name) {
        return name == null ? null : mappings.getOrDefault(name, name);
    }

    @Override
    public final ClassInfo unmap(ClassInfo name) {
        return name == null ? null : unMappings.getOrDefault(name, name);
    }

    @Override
    public final @NotNull ClassFile edit(@NotNull ClassFile file) {
        {
            file.info.name = map(file.info.name);
            file.info.superName = map(file.info.superName);
            if (file.info.interfaces != null)
                for (int i = 0; i < file.info.interfaces.length; i++)
                    file.info.interfaces[i] = map(file.info.interfaces[i]);
            if (file.info.signature != null)
                mapSignature(file.info.signature);
        }
        if (file.main != null) {
            if (file.main.fields != null)
                for (Field field : file.main.fields) mapField(field);
            if (file.main.methods != null)
                for (Method method : file.main.methods) mapMethod(method);
            if (file.main.recordComponents != null) {
                for (ClassFile.RecordComponent recordComponent : file.main.recordComponents)
                    mapRecordComponent(recordComponent);
            }
        }
        if (file.nested != null) {
            if (file.nested.innerClasses != null)
                for (ClassFile.InnerClass innerClass : file.nested.innerClasses) {
                    innerClass.info = map(innerClass.info);
                    innerClass.outerInfo = map(innerClass.outerInfo);
                }
            if (file.nested.enclosingMethod != null) {
                file.nested.enclosingMethod.clazz = map(file.nested.enclosingMethod.clazz);
                mapDescriptor(file.nested.enclosingMethod.desc);
            }
            if (file.nested.nestHost != null)
                file.nested.nestHost = map(file.nested.nestHost);
            if (file.nested.nestMembers != null)
                for (int i = 0; i < file.nested.nestMembers.length; i++)
                    file.nested.nestMembers[i] = map(file.nested.nestMembers[i]);
            if (file.nested.permittedSubclasses != null)
                for (int i = 0; i < file.nested.permittedSubclasses.length; i++)
                    file.nested.permittedSubclasses[i] = map(file.nested.permittedSubclasses[i]);
        }
        if (file.annotation != null) {
            if (file.annotation.visible != null) {
                for (Annotation annotation : file.annotation.visible) mapAnnotation(annotation);
            }
            if (file.annotation.invisible != null) {
                for (Annotation annotation : file.annotation.invisible) mapAnnotation(annotation);
            }
            if (file.annotation.visibleType != null) {
                for (AnnotationType annotation : file.annotation.visibleType) mapAnnotation(annotation);
            }
            if (file.annotation.invisibleType != null) {
                for (AnnotationType annotation : file.annotation.invisibleType) mapAnnotation(annotation);
            }
        }
        return file;
    }

    private void mapField(Field field) {
        {
            mapDescriptor(field.info.info.desc);
            if (field.info.signature != null)
                mapSignature(field.info.signature);
        }
        if (field.annotation != null) {
            if (field.annotation.visible != null)
                for (Annotation annotation : field.annotation.visible) mapAnnotation(annotation);
            if (field.annotation.invisible != null)
                for (Annotation annotation : field.annotation.invisible) mapAnnotation(annotation);
            if (field.annotation.visibleType != null)
                for (AnnotationType annotation : field.annotation.visibleType) mapAnnotation(annotation);
            if (field.annotation.invisibleType != null)
                for (AnnotationType annotation : field.annotation.invisibleType) mapAnnotation(annotation);
        }
    }
    private void mapMethod(Method method) {
        {
            mapDescriptor(method.info.info.desc);
            if (method.info.signature != null) mapSignature(method.info.signature);
            if (method.info.annotationDefault != null) mapElementValue(method.info.annotationDefault);
            if (method.info.exceptions != null) for (int i = 0; i < method.info.exceptions.length; i++) method.info.exceptions[i] = map(method.info.exceptions[i]);
        }
        if (method.code != null) {
            for (Instruction instruction : method.code.instructions) mapInstruction(instruction);
            for (Method.ExceptionHandler exceptionHandler : method.code.exceptionHandlers) exceptionHandler.catchType = map(exceptionHandler.catchType);
            if (method.code.stackMapTable != null)
                for (StackMapFrame stackMapFrame : method.code.stackMapTable) mapStackFrame(stackMapFrame);
            if (method.code.localVariableTable != null)
                for (Method.LocalVariable localVariable : method.code.localVariableTable) mapDescriptor(localVariable.desc);
            if (method.code.localVariableTypeTable != null)
                for (Method.LocalVariableType localVariable : method.code.localVariableTypeTable) mapSignature(localVariable.signature);
            if (method.code.annotation != null) {
                if (method.code.annotation.visible != null) {
                    for (AnnotationType annotation : method.code.annotation.visible) mapAnnotation(annotation);
                }
                if (method.code.annotation.invisible != null) {
                    for (AnnotationType annotation : method.code.annotation.invisible) mapAnnotation(annotation);
                }
            }
        }
        if (method.annotation != null) {
            if (method.annotation.visible != null) {
                for (Annotation annotation : method.annotation.visible) mapAnnotation(annotation);
            }
            if (method.annotation.invisible != null) {
                for (Annotation annotation : method.annotation.invisible) mapAnnotation(annotation);
            }
            if (method.annotation.visibleParameter != null) {
                for (Annotation[] annotations : method.annotation.visibleParameter) for ( Annotation annotation : annotations) mapAnnotation(annotation);
            }
            if (method.annotation.invisibleParameter != null) {
                for (Annotation[] annotations : method.annotation.invisibleParameter) for ( Annotation annotation : annotations) mapAnnotation(annotation);
            }
            if (method.annotation.visibleType != null) {
                for (AnnotationType annotation : method.annotation.visibleType) mapAnnotation(annotation);
            }
            if (method.annotation.invisibleType != null) {
                for (AnnotationType annotation : method.annotation.invisibleType) mapAnnotation(annotation);
            }
        }
    }
    private void mapRecordComponent(ClassFile.RecordComponent component) {
        {
            mapDescriptor(component.info.info.desc);
            if (component.info.signature != null) mapSignature(component.info.signature);
        }
        if (component.annotation != null) {
            if (component.annotation.visible != null) {
                for (Annotation annotation : component.annotation.visible) mapAnnotation(annotation);
            }
            if (component.annotation.invisible != null) {
                for (Annotation annotation : component.annotation.invisible) mapAnnotation(annotation);
            }
            if (component.annotation.visibleType != null) {
                for (AnnotationType annotation : component.annotation.visibleType) mapAnnotation(annotation);
            }
            if (component.annotation.invisibleType != null) {
                for (AnnotationType annotation : component.annotation.invisibleType) mapAnnotation(annotation);
            }
        }
    }
    private void mapInstruction(Instruction instruction) {
        if (instruction instanceof Instruction.LoadConst) {
            mapConstant(((Instruction.LoadConst) instruction).constant);
            if (((Instruction.LoadConst) instruction).constant instanceof ClassFile.BootstrapMethod) {
                for (Object arg : ((ClassFile.BootstrapMethod) ((Instruction.LoadConst) instruction).constant).args) mapConstant(arg);
                mapDescriptor(((Instruction.LoadConst) instruction).info.desc);
            }
        } else if (instruction instanceof Instruction.AccessField) {
            mapConstant(((Instruction.AccessField) instruction).info);
        } else if (instruction instanceof Instruction.InvokeMethod) {
            mapConstant(((Instruction.InvokeMethod) instruction).info);
        } else if (instruction instanceof Instruction.InvokeDynamic) {
            mapConstant(((Instruction.InvokeDynamic) instruction).method);
            for (Object arg : ((Instruction.InvokeDynamic) instruction).method.args) mapConstant(arg);
            mapDescriptor(((Instruction.InvokeDynamic) instruction).info.desc);
        } else if (instruction instanceof Instruction.New)
            ((Instruction.New) instruction).info = map(((Instruction.New) instruction).info);
        else if (instruction instanceof Instruction.CheckCast)
            ((Instruction.CheckCast) instruction).info = map(((Instruction.CheckCast) instruction).info);
        else if (instruction instanceof Instruction.InstanceOf)
            ((Instruction.InstanceOf) instruction).info = map(((Instruction.InstanceOf) instruction).info);

    }
    private void mapStackFrame(StackMapFrame frame) {
        if (frame instanceof StackMapFrameAppend)
            for (StackMapFrame.VerificationInfo local : ((StackMapFrameAppend) frame).locals) mapVerificationInfo(local);
        else if (frame instanceof StackMapFrameFull) {
            for (StackMapFrame.VerificationInfo local : ((StackMapFrameFull) frame).locals) mapVerificationInfo(local);
            for (StackMapFrame.VerificationInfo stackEntry : ((StackMapFrameFull) frame).stack) mapVerificationInfo(stackEntry);
        } else if (frame instanceof StackMapFrameSameLocals) {
            mapVerificationInfo(((StackMapFrameSameLocals) frame).stack);
        } else if (frame instanceof StackMapFrameSameLocalsExtended) {
            mapVerificationInfo(((StackMapFrameSameLocalsExtended) frame).stack);
        }
    }
    private void mapVerificationInfo(StackMapFrame.VerificationInfo info) {
        if (info instanceof StackMapFrame.VerificationInfo.Object)
            ((StackMapFrame.VerificationInfo.Object) info).info = map(((StackMapFrame.VerificationInfo.Object) info).info);
    }
    private void mapSignature(ISignature signature) {
        if (signature instanceof SignatureClass) {
            SignatureClass signatureClass = (SignatureClass) signature;
            if (!signatureClass.isFieldDescriptor) {
                if (signatureClass.typeParameters != null) {
                    for (SignatureTypeParameterType typeParameter : signatureClass.typeParameters) {
                        mapSignatureTypePart(typeParameter.clazz);
                        if (typeParameter.interfaces != null)
                            for (ISignatureReferencePart anInterface : typeParameter.interfaces)
                                mapSignatureTypePart(anInterface);
                    }
                }
                if (signatureClass.interfaceTypes != null)
                    for (ISignatureReferencePart anInterface : signatureClass.interfaceTypes)
                        mapSignatureTypePart(anInterface);
            }
            mapSignatureTypePart(signatureClass.fieldType);

        } else {
            SignatureMethod signatureMethod = (SignatureMethod) signature;
            if (signatureMethod.typeParameters != null)
                for (SignatureTypeParameterType typeParameter : signatureMethod.typeParameters) {
                    mapSignatureTypePart(typeParameter.clazz);
                    if (typeParameter.interfaces != null)
                        for (ISignatureReferencePart anInterface : typeParameter.interfaces)
                            mapSignatureTypePart(anInterface);
                }
            if (signatureMethod.arguments != null)
                for (ISignatureJavaTypePart argument : signatureMethod.arguments) {
                    mapSignatureTypePart(argument);
                }
            if (signatureMethod.exceptions != null)
                for (ISignatureThrowsPart exception : signatureMethod.exceptions) {
                    mapSignatureTypePart(exception);
                }
            mapSignatureTypePart(signatureMethod.result);
        }
    }
    private void mapSignatureTypePart(ISignatureReturnPart part) {
        if (part instanceof SignatureArrayType)
            mapSignatureTypePart(((SignatureArrayType) part).arrayType);
        else if (part instanceof SignatureClassType) {
            SignatureClassType fPart = (SignatureClassType) part;
            String pkgString = fPart.pkg == null ? "" : String.join(".", fPart.pkg) + ".";
            StringBuilder namesString = new StringBuilder();
            for (SignatureClassType.GenericClassInfo aClass : fPart.classes) {
                ClassInfo info = new ClassInfo(pkgString + namesString + aClass.name);
                ClassInfo mapped = map(info);
                if (mapped != info) {
                    aClass.name = mapped.name.lastIndexOf("$") == -1 ? mapped.name : mapped.name.substring(mapped.name.lastIndexOf("$") + 1);
                    fPart.pkg = mapped.pkg;
                    pkgString = fPart.pkg == null ? "" : String.join(".", fPart.pkg) + ".";
                }
                namesString.append("$").append(aClass.name);
            }
        }
    }
    private void mapDescriptor(IDescriptor descriptor) {
        if (descriptor instanceof DescriptorField) {
            DescriptorField descriptorField = (DescriptorField) descriptor;
            mapDescriptorTypePart(descriptorField.type);
        } else {
            DescriptorMethod descriptorMethod = (DescriptorMethod) descriptor;
            if (descriptorMethod.params != null)
                for (IDescriptorFieldPart param : descriptorMethod.params) mapDescriptorTypePart(param);
            mapDescriptorTypePart(descriptorMethod.returnType);
        }
    }
    private void mapDescriptorTypePart(IDescriptorReturnPart part) {
        if (part instanceof DescriptorArrayType)
            mapDescriptorTypePart(((DescriptorArrayType) part).arrayType);
        else if (part instanceof DescriptorObjectType) {
            ClassInfo info = map(((DescriptorObjectType) part));
            ((DescriptorObjectType) part).name = info.name;
            ((DescriptorObjectType) part).pkg = info.pkg;
        }
    }
    private void mapAnnotation(Annotation annotation) {
        mapDescriptor(annotation.type);
        for (ElementValuePair pair : annotation.pairs) mapElementValue(pair.value);
    }
    private void mapElementValue(ElementValuePair.ElementValue elementValue) {
        Object value = elementValue.value;
        if (value instanceof ClassInfo)
            elementValue.value = map(((ClassInfo) value));
        else if (value instanceof FieldInfo)
            mapDescriptor(((FieldInfo) value).desc);
        else if (value instanceof Annotation)
            mapAnnotation((Annotation) value);
        else if (value instanceof ElementValuePair.ElementValue[])
            for (ElementValuePair.ElementValue elementValue1 : ((ElementValuePair.ElementValue[]) value))
                mapElementValue(elementValue1);
    }
    private void mapConstant(Object constant) {
        if (constant instanceof ClassInfo) {
            ClassInfo mapped = map((ClassInfo) constant);
            ((ClassInfo) constant).name = mapped.name;
            ((ClassInfo) constant).pkg = mapped.pkg;
        } else if (constant instanceof ClassFieldInfo){
            ((ClassFieldInfo) constant).clazz = map(((ClassFieldInfo) constant).clazz);
            mapDescriptor(((ClassFieldInfo) constant).desc);
        } else if (constant instanceof ClassMethodInfo) {
            ((ClassMethodInfo) constant).clazz = map(((ClassMethodInfo) constant).clazz);
            mapDescriptor(((ClassMethodInfo) constant).desc);
        } else if (constant instanceof FieldInfo)
            mapDescriptor(((FieldInfo) constant).desc);
        else if (constant instanceof MethodInfo)
            mapDescriptor(((MethodInfo) constant).desc);
        else if (constant instanceof MethodHandleInfo)
            mapConstant(((MethodHandleInfo) constant).getInfo());
        else if (constant instanceof DescriptorMethod)
            mapDescriptor((DescriptorMethod) constant);
    }
}
