package me.lowedermine.jareditor.jar.descriptors;

import me.lowedermine.jareditor.jar.infos.ClassInfo;

public class ObjectDescriptorType extends ClassInfo implements IDescriptorFieldPart {

    public ObjectDescriptorType(String name) {
        super(name);
    }

    @Override
    public String toRaw() {
        return "L" + super.toRaw() + ";";
    }
}
