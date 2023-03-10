package me.lowedermine.jareditor.exceptions;

import me.lowedermine.jareditor.jar.infos.ClassInfo;

public class CodeEditingException extends RuntimeException{
    public CodeEditingException() {
        super();
    }

    public CodeEditingException(String message) {
        super(message);
    }

    public CodeEditingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeEditingException(ClassInfo hub, ClassInfo preload, Throwable cause) {
        super(preload == null ? "Preload hub \"" + hub.toRaw().replace('/', '.') + "\" cannot be loaded" : "Preload hub \"" + hub.toRaw().replace('/', '.') + "\" preload \"" + preload.toRaw().replace('/', '.') + "\" cannot be loaded", cause);
    }
}
