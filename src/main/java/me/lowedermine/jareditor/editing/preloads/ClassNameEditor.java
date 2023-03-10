package me.lowedermine.jareditor.editing.preloads;

import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.Field;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.ElementValuePair;
import me.lowedermine.jareditor.jar.annotations.TypeAnnotation;
import me.lowedermine.jareditor.jar.code.instruction.Instruction;
import me.lowedermine.jareditor.jar.code.stackmapframe.*;
import me.lowedermine.jareditor.jar.descriptors.*;
import me.lowedermine.jareditor.jar.infos.*;
import me.lowedermine.jareditor.jar.signatures.*;

import java.util.HashMap;
import java.util.Map;

public abstract class ClassNameEditor extends NameEditor<ClassInfo, ClassInfo> {
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
    public final ClassFile apply(ClassFile file) {
        if (file == null) {
            return null;
        }
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
                for (TypeAnnotation annotation : file.annotation.visibleType) mapAnnotation(annotation);
            }
            if (file.annotation.invisibleType != null) {
                for (TypeAnnotation annotation : file.annotation.invisibleType) mapAnnotation(annotation);
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
                for (TypeAnnotation annotation : field.annotation.visibleType) mapAnnotation(annotation);
            if (field.annotation.invisibleType != null)
                for (TypeAnnotation annotation : field.annotation.invisibleType) mapAnnotation(annotation);
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
                    for (TypeAnnotation annotation : method.code.annotation.visible) mapAnnotation(annotation);
                }
                if (method.code.annotation.invisible != null) {
                    for (TypeAnnotation annotation : method.code.annotation.invisible) mapAnnotation(annotation);
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
                for (TypeAnnotation annotation : method.annotation.visibleType) mapAnnotation(annotation);
            }
            if (method.annotation.invisibleType != null) {
                for (TypeAnnotation annotation : method.annotation.invisibleType) mapAnnotation(annotation);
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
                for (TypeAnnotation annotation : component.annotation.visibleType) mapAnnotation(annotation);
            }
            if (component.annotation.invisibleType != null) {
                for (TypeAnnotation annotation : component.annotation.invisibleType) mapAnnotation(annotation);
            }
        }
    }
    private void mapInstruction(Instruction instruction) {
        if (instruction instanceof Instruction.LoadConstant) {
            mapConstant(((Instruction.LoadConstant) instruction).constant);
            if (((Instruction.LoadConstant) instruction).constant instanceof ClassFile.BootstrapMethod) {
                for (Object arg : ((ClassFile.BootstrapMethod) ((Instruction.LoadConstant) instruction).constant).args) mapConstant(arg);
                mapDescriptor(((Instruction.LoadConstant) instruction).info.desc);
            }
        } else if (instruction instanceof Instruction.Field) {
            mapConstant(((Instruction.Field) instruction).info);
        } else if (instruction instanceof Instruction.Method) {
            mapConstant(((Instruction.Method) instruction).info);
        } else if (instruction instanceof Instruction.DynamicMethod) {
            mapConstant(((Instruction.DynamicMethod) instruction).method);
            for (Object arg : ((Instruction.DynamicMethod) instruction).method.args) mapConstant(arg);
            mapDescriptor(((Instruction.DynamicMethod) instruction).info.desc);
        } else if (instruction instanceof Instruction.New)
            ((Instruction.New) instruction).info = map(((Instruction.New) instruction).info);
        else if (instruction instanceof Instruction.Cast)
            ((Instruction.Cast) instruction).info = map(((Instruction.Cast) instruction).info);
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
        if (signature instanceof ClassSignature) {
            ClassSignature classSignature = (ClassSignature) signature;
            if (!classSignature.isFieldDescriptor) {
                if (classSignature.typeParameters != null) {
                    for (TypeParameterSignatureType typeParameter : classSignature.typeParameters) {
                        mapSignatureTypePart(typeParameter.clazz);
                        if (typeParameter.interfaces != null)
                            for (ISignatureReferencePart anInterface : typeParameter.interfaces)
                                mapSignatureTypePart(anInterface);
                    }
                }
                if (classSignature.interfaceTypes != null)
                    for (ISignatureReferencePart anInterface : classSignature.interfaceTypes)
                        mapSignatureTypePart(anInterface);
            }
            mapSignatureTypePart(classSignature.fieldType);

        } else {
            MethodSignature methodSignature = (MethodSignature) signature;
            if (methodSignature.typeParameters != null)
                for (TypeParameterSignatureType typeParameter : methodSignature.typeParameters) {
                    mapSignatureTypePart(typeParameter.clazz);
                    if (typeParameter.interfaces != null)
                        for (ISignatureReferencePart anInterface : typeParameter.interfaces)
                            mapSignatureTypePart(anInterface);
                }
            if (methodSignature.arguments != null)
                for (ISignatureJavaTypePart argument : methodSignature.arguments) {
                    mapSignatureTypePart(argument);
                }
            if (methodSignature.exceptions != null)
                for (ISignatureThrowsPart exception : methodSignature.exceptions) {
                    mapSignatureTypePart(exception);
                }
            mapSignatureTypePart(methodSignature.result);
        }
    }
    private void mapSignatureTypePart(ISignatureReturnPart part) {
        if (part instanceof ArraySignatureType)
            mapSignatureTypePart(((ArraySignatureType) part).arrayType);
        else if (part instanceof ClassSignatureType) {
            ClassSignatureType fPart = (ClassSignatureType) part;
            String pkgString = fPart.pkg == null ? "" : fPart.pkg.toRaw() + ".";
            StringBuilder namesString = new StringBuilder();
            for (ClassSignatureType.GenericClassInfo aClass : fPart.classes) {
                ClassInfo info = new ClassInfo(pkgString + namesString + aClass.name);
                ClassInfo mapped = map(info);
                if (mapped != info) {
                    aClass.name = mapped.name.lastIndexOf("$") == -1 ? mapped.name : mapped.name.substring(mapped.name.lastIndexOf("$") + 1);
                    fPart.pkg = mapped.pkg;
                    pkgString = fPart.pkg == null ? "" : fPart.pkg.toRaw() + ".";
                }
                namesString.append("$").append(aClass.name);
            }
        }
    }
    private void mapDescriptor(IDescriptor descriptor) {
        if (descriptor instanceof FieldDescriptor) {
            FieldDescriptor fieldDescriptor = (FieldDescriptor) descriptor;
            mapDescriptorTypePart(fieldDescriptor.type);
        } else {
            MethodDescriptor methodDescriptor = (MethodDescriptor) descriptor;
            if (methodDescriptor.params != null)
                for (IDescriptorFieldPart param : methodDescriptor.params) mapDescriptorTypePart(param);
            mapDescriptorTypePart(methodDescriptor.returnType);
        }
    }
    private void mapDescriptorTypePart(IDescriptorReturnPart part) {
        if (part instanceof ArrayDescriptorType)
            mapDescriptorTypePart(((ArrayDescriptorType) part).arrayType);
        else if (part instanceof ObjectDescriptorType) {
            ClassInfo info = map(((ObjectDescriptorType) part));
            ((ObjectDescriptorType) part).name = info.name;
            ((ObjectDescriptorType) part).pkg = info.pkg;
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
        else if (constant instanceof MethodDescriptor)
            mapDescriptor((MethodDescriptor) constant);
    }
}
