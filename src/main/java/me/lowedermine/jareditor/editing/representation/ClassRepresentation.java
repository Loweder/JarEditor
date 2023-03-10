package me.lowedermine.jareditor.editing.representation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ClassRepresentation {
    String value() default "";
    String[] pkg() default {};
}
