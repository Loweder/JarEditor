package me.lowedermine.jareditor.editing.representative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface MethodRepresentative {
    String name() default "";
}
