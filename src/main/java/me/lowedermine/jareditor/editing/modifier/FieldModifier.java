package me.lowedermine.jareditor.editing.modifier;

public @interface FieldModifier {
    Mode value() default Mode.EDIT;
    String target();
    EditMode edit() default EditMode.ALL;
    enum Mode {
        CREATE, DESTROY, EDIT
    }
    enum EditMode {
        ALL, ACCESS, NAME, VALUE, DESCRIPTOR, ANNOTATION
    }
}
