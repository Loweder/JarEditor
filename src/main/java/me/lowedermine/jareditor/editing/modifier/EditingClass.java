package me.lowedermine.jareditor.editing.modifier;

import me.lowedermine.jareditor.EditingClassloader;
import me.lowedermine.jareditor.editing.preloads.IEditor;
import me.lowedermine.jareditor.exceptions.CodeEditingException;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.ClassFileAccessingUtils;
import me.lowedermine.jareditor.jar.Field;
import me.lowedermine.jareditor.jar.Method;
import me.lowedermine.jareditor.jar.annotations.Annotation;
import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.FieldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: Field and method access changing
//  - Add "non-compatible with Reflection API" warning
public class EditingClass extends ClassFile implements IEditor {
    public boolean modifying = false;
    public ClassInfo[] appliedTo;
    public EditorCodeData[] codeBase;
    public Mode mode = null;
    public boolean representing = false;
    public ClassFile representation = null;

    public EditingClass(ClassFile source) {
        super(source);
    }

    @Override
    public void acceptClassloader(@NotNull EditingClassloader loader) {
        ClassFileAccessingUtils accessor = new ClassFileAccessingUtils(loader);
        if (annotation != null && annotation.invisible != null) {
            FieldDescriptor modifierDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/modifier/ClassModifier;");
            FieldDescriptor representativeDescriptor = new FieldDescriptor("Lme/lowedermine/jareditor/editing/representation/ClassRepresentation;");
            Annotation modifierAnnotation = accessor.annotation(this, modifierDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            Annotation representativeAnnotation = accessor.annotation(this, representativeDescriptor, ClassFileAccessingUtils.AnnotationType.NORMAL_INVISIBLE);
            List<Annotation> listAnnotations = new ArrayList<>(Arrays.asList(annotation.invisible));
            if (modifierAnnotation != null) {
                listAnnotations.remove(modifierAnnotation);
                modifying = true;
                Annotation[] rawAppliedTo = Arrays.stream((Object[]) accessor.annotationValue(modifierAnnotation, "targets")).map(o -> (Annotation)o).toArray(Annotation[]::new);
                List<ClassInfo> listAppliedTo = new ArrayList<>();
                for (Annotation annotation : rawAppliedTo) {
                    String name = (String) accessor.annotationValue(annotation, "value");
                    String[] pkg = Arrays.stream((Object[]) accessor.annotationValue(annotation, "pkg")).map(o -> (String)o).toArray(String[]::new);
                    String stringPkg = pkg.length == 0 ? "" : String.join("/", pkg) + "/";
                    listAppliedTo.add(new ClassInfo(stringPkg + name));
                }
                appliedTo = listAppliedTo.isEmpty() ? null : listAppliedTo.toArray(new ClassInfo[0]);
                String mode = ((FieldInfo) accessor.annotationValue(modifierAnnotation, "value")).name;
                switch (mode) {
                    case "CREATE":
                        this.mode = Mode.CREATE;
                        if (appliedTo == null || appliedTo.length != 1)
                            throw new CodeEditingException("Editing class with mode CREATE must specify exactly one target");
                        break;
                    case "DESTROY":
                        this.mode = Mode.DESTROY;
                        if (appliedTo == null || appliedTo.length < 1)
                            throw new CodeEditingException("Editing class with mode DESTROY must specify at least one target");
                        break;
                    case "EDIT":
                        this.mode = Mode.EDIT;
                        break;
                    case "CODE_EDIT":
                        this.mode = Mode.CODE_EDIT;
                        break;
                }
            }
            if (representativeAnnotation != null) {
                listAnnotations.remove(representativeAnnotation);
                representing = true;
                String name = (String) accessor.annotationValue(representativeAnnotation, "value");
                String[] pkg = Arrays.stream((Object[]) accessor.annotationValue(representativeAnnotation, "pkg")).map(o -> (String)o).toArray(String[]::new);
                String stringPkg = pkg.length == 0 ? "" : String.join("/", pkg) + "/";
                representation = accessor.forName(new ClassInfo(stringPkg + name));
                if (representation == null)
                    throw new CodeEditingException("Class representation points to invalid class: \"" +  stringPkg + name + "\"");
            }
            annotation.invisible = listAnnotations.toArray(new Annotation[0]);
            recalculateSegments();
        }
        if (main != null) {
            if (main.fields != null) {
                for (int i = 0; i < main.fields.length; i++) {
                    main.fields[i] = new EditingField(this, main.fields[i], accessor);
                }
            }
            if (main.methods != null) {
                for (int i = 0; i < main.methods.length; i++) {
                    main.methods[i] = new EditingMethod(this, main.methods[i], accessor);
                }
            }
        }
        if (this.mode == Mode.CODE_EDIT)
            if (main != null) {
                if (main.methods != null) {
                    List<EditorCodeData> listCodes = new ArrayList<>();
                    for (int i = 0; i < main.methods.length; i++) {
                        EditorCodeData data = new EditorCodeData(this, main.methods[i], accessor);
                        if (data.isValid)
                            listCodes.add(data);
                    }
                    codeBase = listCodes.toArray(new EditorCodeData[0]);
                }
            }
    }

    @Override
    public @Nullable ClassInfo[] getEdited() {
        return appliedTo;
    }

    public @Nullable ClassFile apply(@Nullable ClassFile file) {
        if (mode == Mode.CREATE) {
            if (file != null)
                return file;
            return new ClassFile(this);
        } else {
            if (file == null || (appliedTo != null && !Arrays.asList(appliedTo).contains(file.info.name)))
                return file;
            if (mode == Mode.DESTROY) {
                return null;
            } else if (mode == Mode.EDIT) {
                if (file.main != null && main != null) {
                    if (file.main.fields != null && main.fields != null) {
                        for (Field field : file.main.fields) {
                            for (Field field1 : main.fields) {
                                EditingField editField = (EditingField) field1;
                                if (editField.modifying && editField.target.equals(field.info.info.name)) {
                                    editField.apply(field);
                                }
                            }
                        }
                    }
                    if (file.main.methods != null && main.methods != null) {
                        for (Method method : file.main.methods) {
                            for (Method method1 : main.methods) {
                                EditingMethod editField = (EditingMethod) method1;
                                if (editField.modifying && editField.target.equals(method.info.info)) {
                                    editField.apply(method);
                                }
                            }
                        }
                    }
                }
            }
        }
        return file;
    }

    enum Mode {
        CREATE, DESTROY, EDIT, CODE_EDIT
    }
}
