package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.exceptions.SignatureException;
import me.lowedermine.jareditor.utils.StringUtils;

public interface ISignatureReferencePart extends ISignatureJavaTypePart {
    static int parseLength(String string) {
        switch (string.charAt(0)) {
            case 'L':
                return 1 + StringUtils.indexOf(string, '<', '>', ';');
            case 'T':
                return 1 + string.indexOf(';');
            case '[':
                return 1 + ISignatureJavaTypePart.parseLength(string.substring(1));
        }
        throw new SignatureException("Invalid signature type: " + string.charAt(0));
    }

    static ISignatureReferencePart parse(String string) {
        switch (string.charAt(0)) {
            case 'L':
                return new ClassSignatureType(string.substring(1, StringUtils.indexOf(string, '<', '>', ';')));
            case 'T':
                return new TypeVariableSignatureType(string.substring(1, string.indexOf(';')));
            case '[':
                return new ArraySignatureType(string.substring(1));
        }
        throw new SignatureException("Invalid signature type: " + string.charAt(0));
    }
}
