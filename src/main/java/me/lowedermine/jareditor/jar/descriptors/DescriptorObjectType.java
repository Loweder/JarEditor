package me.lowedermine.jareditor.jar.descriptors;

import me.lowedermine.jareditor.jar.infos.ClassInfo;

public class DescriptorObjectType extends ClassInfo implements IDescriptorFieldPart {

    public DescriptorObjectType(String name) {
        super(name);
    }

    @Override
    public String toRaw() {
        return "L" + super.toRaw() + ";";
    }
}
