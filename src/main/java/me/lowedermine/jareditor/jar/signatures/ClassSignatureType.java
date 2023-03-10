package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.jar.infos.PackageInfo;
import me.lowedermine.jareditor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassSignatureType implements ISignatureReferencePart, ISignatureThrowsPart {
    public PackageInfo pkg;
    public GenericClassInfo[] classes;

    public ClassSignatureType(String string) {
        int endIndex = string.indexOf('.');
        endIndex = endIndex == -1 || string.indexOf('<') < endIndex ? string.indexOf('<') : endIndex;
        int nameIndex = endIndex == -1 ? string.lastIndexOf('/') : string.substring(0, endIndex).lastIndexOf('/');
        nameIndex = nameIndex == -1 ? 0 : nameIndex;
        pkg = new PackageInfo(string.substring(0, nameIndex));
        if (pkg.name.length == 0) pkg = null;
        String edited = string.substring(nameIndex + 1);
        String[] parts = StringUtils.split(edited, '<', '>', '.');
        List<GenericClassInfo> rawClasses = new ArrayList<>();
        for (String part : parts) {
            rawClasses.add(new GenericClassInfo(part));
        }
        classes = rawClasses.toArray(new GenericClassInfo[0]);
    }

    @Override
    public String toRaw() {
        String pkgString = pkg == null ? "" : pkg.toRaw().replace('.', '/') + "/";
        String classesString = Arrays.stream(classes).map(GenericClassInfo::toRaw).collect(Collectors.joining("."));
        return "L" + pkgString + "/" + classesString + ";";
    }

    public static class GenericClassInfo {
        public String name;
        public ISignatureTypeArgumentsPart[] typeArguments;

        public GenericClassInfo(String string) {
            int typesIndex = string.indexOf('<');
            if (typesIndex == -1) {
                name = string;
                typeArguments = null;
            } else {
                name = string.substring(0, typesIndex);
                List<ISignatureTypeArgumentsPart> listTypes = new ArrayList<>();
                String rawTypes = string.substring(typesIndex + 1, string.length() - 1);
                for (int i = 0; i < rawTypes.length();) {
                    listTypes.add(ISignatureTypeArgumentsPart.parse(rawTypes.substring(i)));
                    i += ISignatureTypeArgumentsPart.parseLength(rawTypes.substring(i));
                }
                typeArguments = listTypes.toArray(new ISignatureTypeArgumentsPart[0]);
            }
        }

        public String toRaw() {
            String typeArgs = typeArguments == null ? "" : Arrays.stream(typeArguments).map(ISignatureTypeArgumentsPart::toRaw).collect(Collectors.joining("", "<", ">"));
            return name + typeArgs;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassSignatureType that = (ClassSignatureType) o;
        return Objects.equals(pkg, that.pkg) && Arrays.equals(classes, that.classes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(pkg);
        result = 31 * result + Arrays.hashCode(classes);
        return result;
    }
}
