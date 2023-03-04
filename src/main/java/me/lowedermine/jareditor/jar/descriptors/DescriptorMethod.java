package me.lowedermine.jareditor.jar.descriptors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DescriptorMethod implements IDescriptor {
    public IDescriptorFieldPart[] params;
    public IDescriptorReturnPart returnType;

    public DescriptorMethod(String string) {
        String unformedParams = string.substring(1, string.indexOf(')'));
        List<IDescriptorFieldPart> listParams = new ArrayList<>();
        for (int i = 0; i < unformedParams.length();) {
            listParams.add(IDescriptorFieldPart.parse(unformedParams.substring(i)));
            i += IDescriptorFieldPart.parseLength(unformedParams.substring(i));
        }
        params = listParams.size() > 0 ? listParams.toArray(new IDescriptorFieldPart[0]) : null;
        returnType = IDescriptorReturnPart.parse(string.substring(string.indexOf(')') + 1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DescriptorMethod that = (DescriptorMethod) o;
        return Arrays.equals(params, that.params) && Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(returnType);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    @Override
    public String toRaw() {
        String paramsString = params != null ? Arrays.stream(params).map(IDescriptorReturnPart::toRaw).collect(Collectors.joining()) : "";
        return "(" + paramsString + ")" + returnType.toRaw();
    }

    @Override
    public DescriptorMethod clone() {
        try {
            DescriptorMethod clone = (DescriptorMethod) super.clone();
            if (params != null) {
                clone.params = params.clone();
                for (int i = 0; i < params.length; i++) clone.params[i] = IDescriptorFieldPart.parse(params[i].toRaw());
            }
            clone.returnType = IDescriptorReturnPart.parse(returnType.toRaw());
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
