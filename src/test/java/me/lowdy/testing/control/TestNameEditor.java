package me.lowdy.testing.control;

import me.lowedermine.jareditor.editing.preloads.MethodNameEditor;
import me.lowedermine.jareditor.jar.descriptors.MethodDescriptor;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.ClassMethodInfo;
import me.lowedermine.jareditor.jar.infos.MethodInfo;

import java.util.HashMap;
import java.util.Map;

public class TestNameEditor extends MethodNameEditor {
    public TestNameEditor() {
        Map<ClassMethodInfo, String> renames = new HashMap<>();
        ClassInfo bukkitClass = new ClassInfo("org/bukkit/craftbukkit/bootstrap/Main");
        renames.put(new ClassMethodInfo(bukkitClass, new MethodInfo("run", new MethodDescriptor("([Ljava/lang/String;)V")), false), "rerun");
        setMappings(renames);
    }
}
