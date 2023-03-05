package me.lowdy.testing.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface Invisibil {
    int value() default 0;
    String str() default "";
    AnEnum enumer() default AnEnum.A;
    int[] array() default {};
    Class<?> clazz() default String.class;

    enum AnEnum {
        A, B, C, D
    }
}
