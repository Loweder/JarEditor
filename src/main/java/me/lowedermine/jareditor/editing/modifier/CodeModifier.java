package me.lowedermine.jareditor.editing.modifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Target(ElementType.METHOD)
public @interface CodeModifier {
    MethodTarget[] targets() default {};
    String pattern() default "";
    Mode value() default Mode.REPLACE;

    enum Mode {
        REPLACE, ON_MATCH, WRAP, INSERT
    }
}
