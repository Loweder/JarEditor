package me.lowdy.testing.control;

import me.lowedermine.jareditor.PreloadHub;
import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.PackageInfo;

public class TestHub extends PreloadHub {
    @Override
    public ClassInfo[] getPreloads() {
        return new ClassInfo[] {
                new ClassInfo("me/lowdy/testing/control/TestNameEditor"),
                new ClassInfo("me/lowdy/testing/control/TestEdit")
        };
    }

    @Override
    public PackageInfo[] getExceptions() {
        return new PackageInfo[] {new PackageInfo("me.lowdy.testing.control")};
    }
}
