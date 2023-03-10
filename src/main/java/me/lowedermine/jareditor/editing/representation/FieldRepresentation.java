package me.lowedermine.jareditor.editing.representation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface FieldRepresentation {
    String value() default "";
}
