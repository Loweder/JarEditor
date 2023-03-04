package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.exceptions.SignatureException;
import me.lowedermine.jareditor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SignatureMethod implements ISignature {
    public SignatureTypeParameterType[] typeParameters;
    public ISignatureJavaTypePart[] arguments;
    public ISignatureReturnPart result;
    public ISignatureThrowsPart[] exceptions;

    public SignatureMethod(String string) {
        try {
            if (string.startsWith("<")) {
                List<SignatureTypeParameterType> listTypeParams = new ArrayList<>();
                String edited = string.substring(1, StringUtils.indexOf(string, '<', '>', '>'));
                for (int i = 0; i < edited.length();) {
                    listTypeParams.add(new SignatureTypeParameterType(edited.substring(i)));
                    i += SignatureTypeParameterType.parseLength(edited.substring(i));
                }
                typeParameters = listTypeParams.size() > 0 ? listTypeParams.toArray(new SignatureTypeParameterType[0]) : null;
                string = string.substring(edited.length() + 2);
            }
            List<ISignatureJavaTypePart> listParams = new ArrayList<>();
            String parameters = string.substring(1, string.indexOf(')'));
            for (int i = 0; i < parameters.length();) {
                listParams.add(ISignatureJavaTypePart.parse(parameters.substring(i)));
                i += ISignatureJavaTypePart.parseLength(parameters.substring(i));
            }
            arguments = listParams.size() > 0 ? listParams.toArray(new ISignatureJavaTypePart[0]) : null;
            string = string.substring(parameters.length() + 2);
            result = ISignatureReturnPart.parse(string);
            string = string.substring(ISignatureReturnPart.parseLength(string));
            if (!string.isEmpty()) {
                List<ISignatureThrowsPart> listThrows = new ArrayList<>();
                for (int i = 0; i < string.length();) {
                    if (string.charAt(i) == '^') {
                        listThrows.add(ISignatureThrowsPart.parse(string.substring(i + 1)));
                        i += 1 + ISignatureThrowsPart.parseLength(string.substring(i + 1));
                    } else {
                        throw new SignatureException("Invalid method signature: Unexpected symbol");
                    }
                }
                exceptions = listThrows.size() > 0 ? listThrows.toArray(new ISignatureThrowsPart[0]) : null;
            }
        } catch (SignatureException e) {
            throw e;
        } catch (Exception e) {
            throw new SignatureException("Invalid method signature: " + e.getMessage());
        }
    }

    @Override
    public String toRaw() {
        String typeParamsString = typeParameters == null ? "" : "<" + Arrays.stream(typeParameters).map(SignatureTypeParameterType::toRaw).collect(Collectors.joining()) + ">";
        String parametersString = arguments == null ? "" : Arrays.stream(arguments).map(ISignatureJavaTypePart::toRaw).collect(Collectors.joining());
        String throwsString = exceptions == null ? "" : Arrays.stream(exceptions).map(ISignatureThrowsPart::toRaw).collect(Collectors.joining("^", "^", ""));
        String resultString = result.toRaw();

        return typeParamsString + "(" + parametersString + ")" + resultString + throwsString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureMethod that = (SignatureMethod) o;
        return Arrays.equals(typeParameters, that.typeParameters) && Arrays.equals(arguments, that.arguments) && Objects.equals(result, that.result) && Arrays.equals(exceptions, that.exceptions);
    }

    @Override
    public int hashCode() {
        int result1 = Objects.hash(result);
        result1 = 31 * result1 + Arrays.hashCode(typeParameters);
        result1 = 31 * result1 + Arrays.hashCode(arguments);
        result1 = 31 * result1 + Arrays.hashCode(exceptions);
        return result1;
    }

    @Override
    public SignatureMethod clone() {
        try {
            SignatureMethod clone = (SignatureMethod) super.clone();
            if (typeParameters != null) for (int i = 0; i < typeParameters.length; i++) {
                clone.typeParameters = typeParameters.clone();
                clone.typeParameters[i] = new SignatureTypeParameterType(typeParameters[i].toRaw());
            }
            if (arguments != null) for (int i = 0; i < arguments.length; i++) {
                clone.arguments = arguments.clone();
                clone.arguments[i] = ISignatureJavaTypePart.parse(arguments[i].toRaw());
            }
            if (exceptions != null) for (int i = 0; i < exceptions.length; i++) {
                clone.exceptions = exceptions.clone();
                clone.exceptions[i] = ISignatureThrowsPart.parse(exceptions[i].toRaw());
            }
            clone.result = ISignatureReturnPart.parse(result.toRaw());
            return clone;
        } catch (Throwable ignored) {
            throw new AssertionError("Should not happen");
        }
    }
}
