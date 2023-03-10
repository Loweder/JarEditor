package me.lowedermine.jareditor.editing.modifier;

import me.lowedermine.jareditor.exceptions.CodeEditingException;
import me.lowedermine.jareditor.jar.ClassFileAccessingUtils;
import me.lowedermine.jareditor.jar.Field;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.infos.FieldInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditingField extends Field {
    public boolean modifying = false;
    public String target = null;
    public Mode mode = null;
    public EditMode editMode = null;
    public boolean representing = false;
    public Field representation = null;
    public EditingField(EditingClass clazz, Field source, ClassFileAccessingUtils accessor) {
        super(source);
        if (clazz.modifying && clazz.mode == EditingClass.Mode.DESTROY)
            throw new CodeEditingException("Editing class with mode DESTROY must do not contain any fields");
        if (annotation != null && annotation.invisible != null) {
            FieldDescriptor modifierDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/modifier/FieldModifier;");
            FieldDescriptor representativeDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/representation/FieldRepresentation;");
            Annotation modifierAnnotation = accessor.annotation(source, modifierDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            Annotation representativeAnnotation = accessor.annotation(source, representativeDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            List<Annotation> listAnnotations = new ArrayList<>(Arrays.asList(annotation.invisible));
            if (modifierAnnotation != null) {
                listAnnotations.remove(modifierAnnotation);
                if (!clazz.modifying)
                    throw new CodeEditingException("Editing class must contain @ClassModifier annotation");
                if (clazz.mode != EditingClass.Mode.EDIT)
                    throw new CodeEditingException("Only editing class with mode EDIT can contain editing fields");
                modifying = true;
                target = (String) accessor.annotationValue(modifierAnnotation, "target");
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
                            this.editMode = EditMode.VALUE;
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
                representation = accessor.field(clazz.representation, name);
                if (representation == null)
                    throw new CodeEditingException("Field representation points to invalid field: \"" + name + "\"");
            }
            annotation.invisible = listAnnotations.toArray(new Annotation[0]);
            recalculateSegments();
        }
    }

    public Field apply(Field field) {
        if (mode == Mode.CREATE) {
            if (field != null)
                return field;
            return new Field(this);
        } else {
            if (mode == Mode.DESTROY) {
                return null;
            }
        }
        return field;
    }

    enum Mode {
        CREATE, DESTROY, EDIT
    }
    enum EditMode {
        ALL, ACCESS, NAME, VALUE, DESCRIPTOR, ANNOTATION
    }
}
