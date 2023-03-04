package me.lowedermine.jareditor.jar.signatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SignatureTypeParameterType {
    public String name;
    public ISignatureReferencePart clazz;
    public ISignatureReferencePart[] interfaces;

    public SignatureTypeParameterType(String string) {
        name = string.substring(0, string.indexOf(':'));
        String edited = string.substring(string.indexOf(':'));
        boolean readingClass = true;
        List<ISignatureReferencePart> listInterfaces = new ArrayList<>();
        for (int i = 0; i < edited.length();) {
            if (edited.charAt(i) == ':') {
                if (readingClass) {
                    if (edited.charAt(i + 1) == ':') {
                        clazz = new SignatureClassType("java/lang/Object");
                        i++;
                    } else {
                        clazz = ISignatureReferencePart.parse(edited.substring(i + 1));
                        i += 1 + ISignatureReferencePart.parseLength(edited.substring(i + 1));
                    }
                    readingClass = false;
                } else {
                    listInterfaces.add(ISignatureReferencePart.parse(edited.substring(i + 1)));
                    i += ISignatureReferencePart.parseLength(edited.substring(i + 1));
                }
            } else {
                break;
            }
        }
        interfaces = listInterfaces.size() == 0 ? null : listInterfaces.toArray(new ISignatureReferencePart[0]);
    }

    public String toRaw() {
        String clazzString = (clazz.equals(new SignatureClassType("java/lang/Object")) && interfaces != null) ? "" : clazz.toRaw();
        String interfacesString = (interfaces == null) ? "" : ":" + Arrays.stream(interfaces).map(ISignatureReferencePart::toRaw).collect(Collectors.joining(":"));
        return name + ":" + clazzString + interfacesString;
    }

    public static int parseLength(String string) {
        int length = string.indexOf(':');
        String edited = string.substring(string.indexOf(':'));
        boolean readingClass = true;
        for (int i = 0; i < edited.length();) {
            if (edited.charAt(i) == ':') {
                if (readingClass) {
                    if (edited.charAt(i + 1) == ':') {
                        length++;
                        i++;
                    } else {
                        length += 1 + ISignatureReferencePart.parseLength(edited.substring(i + 1));
                        i += 1 + ISignatureReferencePart.parseLength(edited.substring(i + 1));
                    }
                    readingClass = false;
                } else {
                    length += 1 + ISignatureReferencePart.parseLength(edited.substring(i + 1));
                    i += 1 + ISignatureReferencePart.parseLength(edited.substring(i + 1));
                }
            } else {
                break;
            }
        }
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureTypeParameterType that = (SignatureTypeParameterType) o;
        return Objects.equals(name, that.name) && Objects.equals(clazz, that.clazz) && Arrays.equals(interfaces, that.interfaces);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, clazz);
        result = 31 * result + Arrays.hashCode(interfaces);
        return result;
    }
}
