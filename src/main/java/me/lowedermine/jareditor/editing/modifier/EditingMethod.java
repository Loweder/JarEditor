package me.lowedermine.jareditor.editing.modifier;

import me.lowedermine.jareditor.exceptions.CodeEditingException;
import me.lowedermine.jareditor.jar.ClassFileAccessingUtils;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;
import me.lowedermine.jareditor.jar.infos.FieldInfo;
import me.lowedermine.jareditor.jar.infos.MethodInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditingMethod extends Method {
    public boolean modifying = false;
    public MethodInfo target = null;
    public Mode mode = null;
    public EditMode editMode = null;
    public boolean representing = false;
    public Method representation = null;
    public EditingMethod(EditingClass clazz, Method source, ClassFileAccessingUtils accessor) {
        super(source);
        if (clazz.modifying && clazz.mode == EditingClass.Mode.DESTROY)
            throw new CodeEditingException("Editing class with mode DESTROY must do not contain any methods");
        if (annotation != null && annotation.invisible != null) {
            FieldDescriptor modifierDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/modifier/MethodModifier;");
            FieldDescriptor representativeDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/representation/MethodRepresentation;");
            Annotation modifierAnnotation = accessor.annotation(source, modifierDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            Annotation representativeAnnotation = accessor.annotation(source, representativeDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            List<Annotation> listAnnotations = new ArrayList<>(Arrays.asList(annotation.invisible));
            if (modifierAnnotation != null) {
                listAnnotations.remove(modifierAnnotation);
                if (!clazz.modifying)
                    throw new CodeEditingException("Editing class must contain @ClassModifier annotation");
                if (clazz.mode != EditingClass.Mode.EDIT)
                    throw new CodeEditingException("Only editing class with mode EDIT can contain editing methods");
                modifying = true;
                Annotation targetAnnotation = (Annotation) accessor.annotationValue(modifierAnnotation, "target");
                String name = (String) accessor.annotationValue(targetAnnotation, "value");
                String desc = (String) accessor.annotationValue(targetAnnotation, "descriptor");
                target = new MethodInfo(name, new MethodDescriptor(desc));
                String mode = ((FieldInfo) accessor.annotationValue(modifierAnnotation, "value")).name;
                switch (mode) {
                    case "CREATE":
                        this.mode = Mode.CREATE;
                        break;
                    case "DESTROY":
                        this.mode = Mode.DESTROY;
                        break;
                    case "EDIT":
                        this.mode = Mode.EDIT;
                        break;
                }
                if (this.mode == Mode.EDIT) {
                    String editMode = ((FieldInfo) accessor.annotationValue(modifierAnnotation, "edit")).name;
                    switch (editMode) {
                        case "ALL":
                            this.editMode = EditMode.ALL;
                            break;
                        case "ACCESS":
                            this.editMode = EditMode.ACCESS;
                            break;
                        case "NAME":
                            this.editMode = EditMode.NAME;
                            break;
                        case "CODE":
                            this.editMode = EditMode.CODE;
                            break;
                        case "DESCRIPTOR":
                            this.editMode = EditMode.DESCRIPTOR;
                            break;
                        case "ANNOTATION":
                            this.editMode = EditMode.ANNOTATION;
                            break;
                    }
                }
            }
            if (representativeAnnotation != null) {
                listAnnotations.remove(representativeAnnotation);
                if (!clazz.representing)
                    throw new CodeEditingException("Class representation must contain @ClassRepresentation annotation");
                representing = true;
                String name = (String) accessor.annotationValue(representativeAnnotation, "value");
                MethodDescriptor desc = new MethodDescriptor((String) accessor.annotationValue(representativeAnnotation, "descriptor"));
                representation = accessor.method(clazz.representation, new MethodInfo(name, desc));
                if (representation == null)
                    throw new CodeEditingException("Method representation points to invalid method: \"" + name + "\"");
            }
            annotation.invisible = listAnnotations.toArray(new Annotation[0]);
            recalculateSegments();
        }
    }

    public Method apply(Method method) {
        if (mode == Mode.CREATE) {
            if (method != null)
                return method;
            return new Method(this);
        } else {
            if (mode == Mode.DESTROY) {
                return null;
            }
        }
        return method;
    }
    enum Mode {
        CREATE, DESTROY, EDIT
    }
    enum EditMode {
        ALL, ACCESS, NAME, CODE, DESCRIPTOR, ANNOTATION
    }
}
