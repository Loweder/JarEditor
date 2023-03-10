package me.lowedermine.jareditor.editing.representation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface MethodRepresentation {
    String value() default "";
    String descriptor() default "()V";
}
