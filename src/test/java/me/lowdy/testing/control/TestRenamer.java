package me.lowdy.testing.control;

import me.lowedermine.jareditor.editing.preloads.editors.MethodRenamer;
import me.lowedermine.jareditor.jar.descriptors.DescriptorMethod;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.ClassMethodInfo;
import me.lowedermine.jareditor.jar.infos.MethodInfo;

import java.util.HashMap;
import java.util.Map;

public class TestRenamer extends MethodRenamer {
    public TestRenamer() {
        Map<ClassMethodInfo, String> renames = new HashMap<>();
        ClassInfo bukkitClass = new ClassInfo("org/bukkit/craftbukkit/bootstrap/Main");
        renames.put(new ClassMethodInfo(bukkitClass, new MethodInfo("run", new DescriptorMethod("([Ljava/lang/String;)V")), false), "rerun");
        setMappings(renames);
    }
}
