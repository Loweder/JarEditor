package me.lowedermine.jareditor.editing.representative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface FieldRepresentative {
    String name() default "";
}
