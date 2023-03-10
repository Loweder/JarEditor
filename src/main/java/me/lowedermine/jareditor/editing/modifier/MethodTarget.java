package me.lowedermine.jareditor.editing.modifier;

import java.lang.annotation.Target;

@Target({})
public @interface MethodTarget {
    String descriptor();
    String value();
}
