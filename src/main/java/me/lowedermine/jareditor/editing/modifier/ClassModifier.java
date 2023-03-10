package me.lowedermine.jareditor.editing.modifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Target(ElementType.TYPE)
public @interface ClassModifier {
    Mode value() default Mode.EDIT;
    ClassTarget[] targets() default {};
    enum Mode {
        CREATE, DESTROY, EDIT, CODE_EDIT
    }
}
