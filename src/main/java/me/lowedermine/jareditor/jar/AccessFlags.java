package me.lowedermine.jareditor.jar;

import java.util.ArrayList;
import java.util.List;

public enum AccessFlags {
    PUBLIC(0x0001, new Type[]{Type.CLASS, Type.METHOD, Type.FIELD, Type.INNERCLASS}),
    PRIVATE(0x0002, new Type[]{Type.METHOD, Type.FIELD, Type.INNERCLASS}),
    PROTECTED(0x0004, new Type[]{Type.METHOD, Type.FIELD, Type.INNERCLASS}),
    STATIC(0x0008, new Type[]{Type.METHOD, Type.FIELD, Type.INNERCLASS}),
    FINAL(0x0010, new Type[]{Type.CLASS, Type.FIELD, Type.METHOD, Type.METHOD_PARAMETER, Type.INNERCLASS}),
    SUPER(0x0020, new Type[]{Type.CLASS}),
    SYNCHRONIZED(0x0020, new Type[]{Type.METHOD}),
    OPEN(0x0020, new Type[]{Type.MODULE}),
    TRANSITIVE(0x0020, new Type[]{Type.MODULE_REQUIRE}),
    BRIDGE(0x0040, new Type[]{Type.METHOD}),
    VOLATILE(0x0040, new Type[]{Type.FIELD}),
    STATIC_PHASE(0x0040, new Type[]{Type.MODULE_REQUIRE}),
    VARARGS(0x0080, new Type[]{Type.METHOD}),
    TRANSIENT(0x0080, new Type[]{Type.FIELD}),
    NATIVE(0x0100, new Type[]{Type.METHOD}),
    INTERFACE(0x0200, new Type[]{Type.CLASS, Type.INNERCLASS}),
    ABSTRACT(0x0400, new Type[]{Type.CLASS, Type.METHOD, Type.INNERCLASS}),
    STRICT(0x0800, new Type[]{Type.METHOD}),
    SYNTHETIC(0x1000, new Type[]{Type.CLASS, Type.METHOD, Type.METHOD_PARAMETER, Type.FIELD, Type.MODULE, Type.MODULE_OPEN, Type.MODULE_EXPORT, Type.INNERCLASS}),
    ANNOTATION(0x2000, new Type[]{Type.CLASS, Type.INNERCLASS}),
    ENUM(0x4000, new Type[]{Type.CLASS, Type.FIELD, Type.INNERCLASS}),
    MODULE(0x8000, new Type[]{Type.CLASS}),
    MANDATED(0x8000, new Type[]{Type.MODULE, Type.MODULE_OPEN, Type.MODULE_EXPORT, Type.MODULE_REQUIRE});

    private final int code;
    private final Type[] types;

    AccessFlags(int code, Type[] types) {
        this.code = code;
        this.types = types;
    }

    public static AccessFlags[] fromCode(int code, Type type) {
        List<AccessFlags> flags = new ArrayList<>();
        for (AccessFlags value : values()) {
            if ((value.code & code) != 0)
                for (Type type1 : value.types) {
                    if (type1 == type) {
                        flags.add(value);
                        break;
                    }
                }
        }
        return flags.toArray(new AccessFlags[0]);
    }

    public static int toCode(AccessFlags[] accessFlags) {
        int flags = 0;
        for (AccessFlags accessFlag : accessFlags) {
            flags |= accessFlag.code;
        }
        return flags;
    }

    public enum Type {
        CLASS, INNERCLASS, FIELD, METHOD, METHOD_PARAMETER, MODULE, MODULE_OPEN, MODULE_EXPORT, MODULE_REQUIRE
    }
}
