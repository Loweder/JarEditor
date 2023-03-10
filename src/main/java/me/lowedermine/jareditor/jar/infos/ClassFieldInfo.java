package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.FieldDescriptor;

public class ClassFieldInfo extends ClassMemberInfo {
    public ClassFieldInfo(ClassInfo inClass, MemberInfo inInfo) {
        super(inClass, inInfo);
    }

    @Override
    public FieldInfo getInfo() {
        return new FieldInfo(name, (FieldDescriptor) desc);
    }
}
