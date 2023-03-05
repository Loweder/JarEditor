package me.lowdy.testing.control;

import me.lowedermine.jareditor.editing.preloads.editors.ClassRenamer;
import me.lowedermine.jareditor.jar.infos.ClassInfo;

import java.util.HashMap;
import java.util.Map;

public class TestRenamer extends ClassRenamer {
    public TestRenamer() {
        Map<ClassInfo, ClassInfo> renames = new HashMap<>();
        renames.put(new ClassInfo("me/lowdy/testing/Executable"), new ClassInfo("me/lowdy/TotallyOther"));
        renames.put(new ClassInfo("me/lowdy/testing/OtherTest"), new ClassInfo("me/lowdy/SomethingElse"));
        setMappings(renames);
    }
}
