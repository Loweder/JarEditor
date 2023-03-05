package me.lowedermine.jareditor.editing.preloads.editors;

import me.lowedermine.jareditor.editing.preloads.Renamer;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.Field;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.annotations.AnnotationType;
import me.lowedermine.jareditor.jar.annotations.ElementValuePair;
import me.lowedermine.jareditor.jar.code.instruction.Instruction;
import me.lowedermine.jareditor.jar.descriptors.DescriptorField;
import me.lowedermine.jareditor.jar.descriptors.DescriptorObjectType;
import me.lowedermine.jareditor.jar.infos.ClassFieldInfo;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.FieldInfo;
import me.lowedermine.jareditor.jar.infos.MethodHandleInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class FieldRenamer extends Renamer<ClassFieldInfo, String> {
    private Map<ClassFieldInfo, String> mappings = new HashMap<>();
    private Map<ClassFieldInfo, String> unMappings = new HashMap<>();

    @Override
    protected final void setMappings(Map<ClassFieldInfo, String> mappings) {
        this.mappings = mappings;
        if (mappings == null) {
            this.mappings = new HashMap<>();
        }
        unMappings = new HashMap<>();
        this.mappings.forEach((k, v) -> unMappings.put(new ClassFieldInfo(k.clazz, new FieldInfo(v, (DescriptorField) k.desc)), k.name));
    }

    @Override
    public final String map(ClassFieldInfo name) {
        return name == null ? null : mappings.getOrDefault(name, name.name);
    }

    @Override
    public final String unmap(ClassFieldInfo name) {
        return name == null ? null : unMappings.getOrDefault(name, name.name);
    }

    @Override
    public final @NotNull ClassFile edit(@NotNull ClassFile file) {
        if (file.main != null) {
            if (file.main.fields != null) {
                for (Field field : file.main.fields)
                    mapField(file.info.name, field);
            }
            if (file.main.methods != null) {
                for (Method method : file.main.methods)
                    mapMethod(method);
            }
            if (file.main.recordComponents != null) {
                for (ClassFile.RecordComponent component : file.main.recordComponents)
                    mapRecordComponent(file.info.name, component);
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

    private void mapField(ClassInfo name, Field field) {
        field.info.info.name = map(new ClassFieldInfo(name, field.info.info));
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
    private void mapMethod(Method method) {
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
        if (instruction instanceof Instruction.LoadConst) {
            mapConstant(((Instruction.LoadConst) instruction).constant);
            if (((Instruction.LoadConst) instruction).constant instanceof ClassFile.BootstrapMethod) {
                for (Object arg : ((ClassFile.BootstrapMethod) ((Instruction.LoadConst) instruction).constant).args)
                    mapConstant(arg);
            }
        } else if (instruction instanceof Instruction.AccessField)
            ((Instruction.AccessField) instruction).info.name = map(((Instruction.AccessField) instruction).info);

    }
    private void mapRecordComponent(ClassInfo name, ClassFile.RecordComponent component) {
        component.info.info.name = map(new ClassFieldInfo(name, component.info.info));
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
        for (ElementValuePair pair : annotation.pairs) mapElementValue(pair.value);
    }
    private void mapElementValue(ElementValuePair.ElementValue elementValue) {
        Object value = elementValue.value;
        switch (elementValue.type) {
            case ENUM:
                ((FieldInfo)value).name = map(new ClassFieldInfo(((DescriptorObjectType)((DescriptorField)((FieldInfo) value).desc).type), new FieldInfo(((FieldInfo) value).name, (DescriptorField) ((FieldInfo) value).desc)));
                break;
            case ANNOTATION:
                mapAnnotation((Annotation) value);
                break;
            case ARRAY:
                for (ElementValuePair.ElementValue elementValue1 : ((ElementValuePair.ElementValue[]) value))
                    mapElementValue(elementValue1);

        }
    }
    private void mapConstant(Object constant) {
        if (constant instanceof ClassFieldInfo){
            ((ClassFieldInfo) constant).name = map((ClassFieldInfo) constant);
        } else if (constant instanceof MethodHandleInfo)
            mapConstant(((MethodHandleInfo) constant).getInfo());
    }
}
