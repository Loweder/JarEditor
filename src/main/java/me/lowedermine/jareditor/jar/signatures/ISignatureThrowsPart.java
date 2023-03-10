package me.lowedermine.jareditor.jar.signatures;

import me.lowedermine.jareditor.exceptions.SignatureException;
import me.lowedermine.jareditor.utils.StringUtils;

public interface ISignatureThrowsPart extends ISignatureReferencePart {
    static int parseLength(String string) {
        switch (string.charAt(0)) {
            case 'L':
                return 1 + StringUtils.indexOf(string, '<', '>', ';');
            case 'T':
                return 1 + string.indexOf(';');
        }
        throw new SignatureException("Invalid signature type: " + string.charAt(0));
    }

    static ISignatureThrowsPart parse(String string) {
        switch (string.charAt(0)) {
            case 'L':
                return new ClassSignatureType(string.substring(1, StringUtils.indexOf(string, '<', '>', ';')));
            case 'T':
                return new TypeVariableSignatureType(string.substring(1, string.indexOf(';')));
        }
        throw new SignatureException("Invalid signature type: " + string.charAt(0));
    }

    String toRaw();
}
