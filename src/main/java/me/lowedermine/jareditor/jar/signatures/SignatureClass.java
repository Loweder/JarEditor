package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.exceptions.SignatureException;
import me.lowedermine.jareditor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SignatureClass implements ISignature {
    public boolean isFieldDescriptor;
    public SignatureTypeParameterType[] typeParameters;
    public ISignatureReferencePart fieldType;
    public SignatureClassType[] interfaceTypes;

    public SignatureClass(String string) throws SignatureException {
        try {
            isFieldDescriptor = true;
            if (string.startsWith("<")) {
                isFieldDescriptor = false;
                List<SignatureTypeParameterType> listTypeParams = new ArrayList<>();
                String edited = string.substring(1, StringUtils.indexOf(string, '<', '>', '>'));
                for (int i = 0; i < edited.length();) {
                    listTypeParams.add(new SignatureTypeParameterType(edited.substring(i)));
                    i += SignatureTypeParameterType.parseLength(edited.substring(i));
                }
                typeParameters = listTypeParams.toArray(new SignatureTypeParameterType[0]);
                string = string.substring(edited.length() + 2);
            } else {
                typeParameters = null;
            }
            fieldType = ISignatureReferencePart.parse(string);
            if (!isFieldDescriptor && !(fieldType instanceof SignatureClassType))
                throw new SignatureException("Invalid class signature: Class signature main type is invalid");
            string = string.substring(ISignatureReferencePart.parseLength(string));
            if (!string.isEmpty()) {
                isFieldDescriptor = false;
                List<SignatureClassType> listInterfaces = new ArrayList<>();
                for (int i = 0; i < string.length();) {
                    SignatureClassType classType = (SignatureClassType) ISignatureReferencePart.parse(string.substring(i));
                    listInterfaces.add(classType);
                    i += ISignatureReferencePart.parseLength(string.substring(i));
                }
                interfaceTypes = listInterfaces.toArray(new SignatureClassType[0]);
            } else {
                interfaceTypes = null;
            }
        } catch (SignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new SignatureException("Invalid " + (isFieldDescriptor ? "field" : "class") + " signature: " + e.getMessage());
        }
    }

    @Override
    public String toRaw() {
        String typeParamsString = typeParameters == null ? "" : "<" + Arrays.stream(typeParameters).map(SignatureTypeParameterType::toRaw).collect(Collectors.joining()) + ">";
        String interfacesString = interfaceTypes == null ? "" : Arrays.stream(interfaceTypes).map(ISignatureJavaTypePart::toRaw).collect(Collectors.joining());
        String mainTypeString = fieldType.toRaw();

        return typeParamsString + mainTypeString + interfacesString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureClass that = (SignatureClass) o;
        return isFieldDescriptor == that.isFieldDescriptor && Arrays.equals(typeParameters, that.typeParameters) && Objects.equals(fieldType, that.fieldType) && Arrays.equals(interfaceTypes, that.interfaceTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(isFieldDescriptor, fieldType);
        result = 31 * result + Arrays.hashCode(typeParameters);
        result = 31 * result + Arrays.hashCode(interfaceTypes);
        return result;
    }

    @Override
    public SignatureClass clone() {
        try {
            SignatureClass clone = (SignatureClass) super.clone();
            if (typeParameters != null) {
                clone.typeParameters = typeParameters.clone();
                for (int i = 0; i < typeParameters.length; i++) clone.typeParameters[i] = new SignatureTypeParameterType(typeParameters[i].toRaw());
            }
            if (interfaceTypes != null) {
                clone.interfaceTypes = interfaceTypes.clone();
                for (int i = 0; i < interfaceTypes.length; i++) clone.interfaceTypes[i] = new SignatureClassType(interfaceTypes[i].toRaw());
            }
            clone.fieldType = ISignatureReferencePart.parse(fieldType.toRaw());
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
