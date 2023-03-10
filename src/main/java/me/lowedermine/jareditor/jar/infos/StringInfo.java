package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.utils.IMyCloneable;

import java.util.Objects;

public class StringInfo implements IMyCloneable {
    public String value;

    public StringInfo(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringInfo that = (StringInfo) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public StringInfo clone() {
        try {
            StringInfo clone = (StringInfo) super.clone();
            clone.value = value;
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
