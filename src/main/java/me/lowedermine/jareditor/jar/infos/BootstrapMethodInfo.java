package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;

public class BootstrapMethodInfo extends BootstrapMemberInfo {
    public BootstrapMethodInfo(int index, MemberInfo inInfo) {
        super(index, inInfo);
    }

    @Override
    public MemberInfo getInfo() {
        return new MethodInfo(name, (DescriptorMethod) desc);
    }
}
