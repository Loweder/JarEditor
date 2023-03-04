package me.lowedermine.jareditor.jar.infos;

import me.lowedermine.jareditor.jar.descriptors.DescriptorField;

public class BootstrapFieldInfo extends BootstrapMemberInfo {
    public BootstrapFieldInfo(int index, MemberInfo inInfo) {
        super(index, inInfo);
    }

    @Override
    public MemberInfo getInfo() {
        return new FieldInfo(name, (DescriptorField) desc);
    }
}
