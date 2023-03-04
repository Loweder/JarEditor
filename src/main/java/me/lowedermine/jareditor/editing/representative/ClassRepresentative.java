package me.lowedermine.jareditor.editing.representative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ClassRepresentative {
    String[] pkg() default {};
    String name() default "";
}
