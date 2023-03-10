package me.lowedermine.jareditor.editing.modifier;

public @interface MethodModifier {
    Mode value() default Mode.EDIT;
    MethodTarget target();
    EditMode edit() default EditMode.ALL;
    enum Mode {
        CREATE, DESTROY, EDIT
    }
    enum EditMode {
        ALL, ACCESS, NAME, CODE, DESCRIPTOR, ANNOTATION
    }
}
