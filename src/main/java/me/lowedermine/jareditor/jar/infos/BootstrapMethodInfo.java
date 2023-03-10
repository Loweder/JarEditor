package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;

public class BootstrapMethodInfo extends BootstrapMemberInfo {
    public BootstrapMethodInfo(int index, MemberInfo inInfo) {
        super(index, inInfo);
    }

    @Override
    public MethodInfo getInfo() {
        return new MethodInfo(name, (MethodDescriptor) desc);
    }
}
