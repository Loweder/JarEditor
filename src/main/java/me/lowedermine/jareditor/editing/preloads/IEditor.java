package me.lowedermine.jareditor.editing.preloads;

import me.lowedermine.jareditor.EditingClassloader;
import me.lowedermine.jareditor.jar.ClassFile;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IEditor {

    default void acceptClassloader(@NotNull EditingClassloader loader) {}

    default @Nullable ClassInfo[] getEdited() {
        return null;
    }

    @Nullable ClassFile apply(@Nullable ClassFile file);
}
