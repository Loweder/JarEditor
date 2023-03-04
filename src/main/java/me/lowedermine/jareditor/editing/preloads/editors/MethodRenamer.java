package me.lowedermine.jareditor.editing.preloads.editors;

import me.lowedermine.jareditor.editing.preloads.Renamer;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.Field;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.AnnotationType;
import me.lowedermine.jareditor.jar.annotations.ElementValuePair;
import me.lowedermine.jareditor.jar.code.instruction.Instruction;
import me.lowedermine.jareditor.jar.descriptors.*;
import me.lowedermine.jareditor.jar.infos.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class MethodRenamer extends Renamer<ClassMethodInfo, String> {
    private Map<ClassMethodInfo, String> mappings = new HashMap<>();
    private Map<ClassMethodInfo, String> unMappings = new HashMap<>();

    @Override
    protected final void setMappings(Map<ClassMethodInfo, String> mappings) {
        this.mappings = mappings;
        if (mappings == null) {
            this.mappings = new HashMap<>();
        }
        this.mappings.forEach((info, s) -> info.interfaceMethod = false);
        unMappings = new HashMap<>();
        this.mappings.forEach((k, v) -> unMappings.put(new ClassMethodInfo(k.clazz, new MethodInfo(v, (DescriptorMethod) k.desc), false), k.name));
    }

    @Override
    public final String map(ClassMethodInfo name) {
        if (name == null) return null;
        ClassMethodInfo mapped = new ClassMethodInfo(name.clazz, name, false);
        return mappings.getOrDefault(mapped, mapped.name);
    }

    @Override
    public final String unmap(ClassMethodInfo name) {
        if (name == null) return null;
        ClassMethodInfo mapped = new ClassMethodInfo(name.clazz, name, false);
        return unMappings.getOrDefault(mapped, mapped.name);
    }

    @Override
    public final @NotNull ClassFile edit(@NotNull ClassFile file) {
        if (file.main != null) {
            if (file.main.fields != null) {
                for (Field field : file.main.fields)
                    mapField(field);
            }
            if (file.main.methods != null) {
                for (Method method : file.main.methods)
                    mapMethod(file.info.name, method);
            }
            if (file.main.recordComponents != null) {
                for (ClassFile.RecordComponent component : file.main.recordComponents)
                    mapRecordComponent(component);
            }
            if (file.main.bootstrapMethods != null) {
                for (ClassFile.BootstrapMethod method : file.main.bootstrapMethods) {
                    mapConstant(method);
                    for (Object arg : method.args) mapConstant(arg);
                }
            }
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
        if (field.annotation != null) {
            if (field.annotation.visible != null) {
                for (Annotation annotation : field.annotation.visible) mapAnnotation(annotation);
            }
            if (field.annotation.invisible != null) {
                for (Annotation annotation : field.annotation.invisible) mapAnnotation(annotation);
            }
            if (field.annotation.visibleType != null) {
                for (AnnotationType annotation : field.annotation.visibleType) mapAnnotation(annotation);
            }
            if (field.annotation.invisibleType != null) {
                for (AnnotationType annotation : field.annotation.invisibleType) mapAnnotation(annotation);
            }
        }
    }
    private void mapMethod(ClassInfo name, Method method) {
        method.info.info.name = map(new ClassMethodInfo(name, method.info.info, false));
        if (method.code != null) {
            for (Instruction instruction : method.code.instructions) mapInstruction(instruction);
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
    private void mapInstruction(Instruction instruction) {
        if (instruction instanceof Instruction.LoadConst)
            mapConstant(((Instruction.LoadConst) instruction).constant);
        else if (instruction instanceof Instruction.InvokeMethod)
            ((Instruction.InvokeMethod) instruction).info.name = map(((Instruction.InvokeMethod) instruction).info);

    }
    private void mapRecordComponent(ClassFile.RecordComponent component) {
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
    private void mapAnnotation(Annotation annotation) {
        for (ElementValuePair pair : annotation.pairs) mapElementValuePair(((DescriptorObjectType)annotation.type.type), pair);
    }
    private void mapElementValuePair(ClassInfo name, ElementValuePair pair) {
        ClassMethodInfo info = new ClassMethodInfo(name, new MethodInfo(pair.name, new DescriptorMethod("()" + mapElementValue(pair.value).toRaw())), false);
        pair.name = map(info);
    }
    private IDescriptorReturnPart mapElementValue(ElementValuePair.ElementValue value) {
        switch (value.type) {
            case BYTE:
                return DescriptorBaseType.BYTE;
            case CHAR:
                return DescriptorBaseType.CHAR;
            case DOUBLE:
                return DescriptorBaseType.DOUBLE;
            case FLOAT:
                return DescriptorBaseType.FLOAT;
            case INT:
                return DescriptorBaseType.INT;
            case LONG:
                return DescriptorBaseType.LONG;
            case SHORT:
                return DescriptorBaseType.SHORT;
            case BOOLEAN:
                return DescriptorBaseType.BOOLEAN;
            case STRING:
                return new DescriptorObjectType("java/lang/String");
            case ENUM:
                return new DescriptorObjectType(((FieldInfo)value.value).desc.toRaw());
            case CLASS:
                return new DescriptorObjectType(((ClassInfo)value.value).toRaw());
            case ANNOTATION:
                mapAnnotation(((Annotation)value.value));
                return new DescriptorObjectType(((Annotation) value.value).type.toRaw());
            case ARRAY:
                return new DescriptorArrayType(mapElementValue(((ElementValuePair.ElementValue[])value.value)[0]).toRaw());
        }
        return new DescriptorObjectType("java/lang/Object");
    }
    private void mapConstant(Object constant) {
        if (constant instanceof ClassMethodInfo){
            ((ClassMethodInfo) constant).name = map((ClassMethodInfo) constant);
        } else if (constant instanceof MethodHandleInfo)
            mapConstant(((MethodHandleInfo) constant).getInfo());
//        else if (constant instanceof BootstrapMethodInfo)
//            ((BootstrapMethodInfo) constant).info.name = map(new ClassMethodInfo(name, ((BootstrapMethodInfo) constant).info));
    }
}
