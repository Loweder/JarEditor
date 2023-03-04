package me.lowedermine.jareditor.editing.preloads;

import me.lowedermine.jareditor.jar.ClassFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IEditor {

    List<String> getEdited();

    @NotNull ClassFile edit(@NotNull ClassFile file);
}
