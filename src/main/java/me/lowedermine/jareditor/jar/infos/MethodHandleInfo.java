package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;
import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;

import java.util.Objects;

public class MethodHandleInfo extends ClassMemberInfo {
    public Type type;
    public boolean interfaceMethod = false;

    protected MethodHandleInfo() {

    }

    public MethodHandleInfo(int type, ClassMemberInfo info) {
        super(info.clazz, info.desc instanceof MethodDescriptor ? new MethodInfo(info.name, (MethodDescriptor) info.desc) : new FieldInfo(info.name, (FieldDescriptor) info.desc));
        this.type = Type.of(type);
        if (info instanceof ClassMethodInfo)
            interfaceMethod = ((ClassMethodInfo) info).interfaceMethod;
    }

    @Override
    public ClassMemberInfo getInfo() {
        return desc instanceof MethodDescriptor ? new ClassMethodInfo(clazz, new MethodInfo(name, (MethodDescriptor) desc), interfaceMethod) : new ClassFieldInfo(clazz, new FieldInfo(name, (FieldDescriptor) desc));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MethodHandleInfo that = (MethodHandleInfo) o;
        return type == that.type;
    }

    @Override
    public MethodHandleInfo clone() {
        return (MethodHandleInfo) super.clone();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    public enum Type {
        GET_FIELD(1), GET_STATIC(2), PUT_FIELD(3), PUT_STATIC(4), INVOKE_VIRTUAL(5), INVOKE_STATIC(6), INVOKE_SPECIAL(7), NEW_INVOKE_SPECIAL(8), INVOKE_INTERFACE(9);

        private final int index;

        Type(int index) {
            this.index = index;
        }

        public static Type of(int index) {
            for (Type value : values()) {
                if (value.index == index)
                    return value;
            }
            return null;
        }

        public int to() {
            return index;
        }
    }
}
