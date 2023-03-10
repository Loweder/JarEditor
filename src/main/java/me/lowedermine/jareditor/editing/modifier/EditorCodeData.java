package me.lowedermine.jareditor.editing.modifier;

import me.lowedermine.jareditor.exceptions.CodeEditingException;
import me.lowedermine.jareditor.jar.ClassFileAccessingUtils;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.infos.FieldInfo;

public class EditorCodeData {
    public boolean isValid = false;
    public Mode mode = null;
    public EditorCodeData(EditingClass clazz, Method source, ClassFileAccessingUtils accessor) {
        if (source.annotation != null && source.annotation.invisible != null) {
            FieldDescriptor codeDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/modifier/CodeModifier;");
            Annotation codeAnnotation = accessor.annotation(source, codeDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            if (codeAnnotation != null) {
                if (!clazz.modifying)
                    throw new CodeEditingException("Editing class must contain @ClassModifier annotation");
                if (clazz.mode != EditingClass.Mode.CODE_EDIT)
                    throw new CodeEditingException("Only editing class with mode CODE_EDIT can contain code editing methods");
                isValid = true;
                String mode = ((FieldInfo) accessor.annotationValue(codeAnnotation, "value")).name;
                switch (mode) {
                    case "REPLACE":
                        this.mode = Mode.REPLACE;
                        break;
                    case "ON_MATCH":
                        this.mode = Mode.ON_MATCH;
                        break;
                    case "WRAP":
                        this.mode = Mode.WRAP;
                        break;
                    case "INSERT":
                        this.mode = Mode.INSERT;
                        break;
                }
            }
        }
    }

    public Method apply(Method method) {
        if (method == null)
            return null;
        return method;
    }
    enum Mode {
        REPLACE, ON_MATCH, WRAP, INSERT
    }
}
