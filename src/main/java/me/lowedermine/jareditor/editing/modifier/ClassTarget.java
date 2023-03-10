package me.lowedermine.jareditor.editing.modifier;

import java.lang.annotation.Target;

@Target({})
public @interface ClassTarget {
    String[] pkg() default {};
    String value();
}
