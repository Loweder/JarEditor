package me.lowedermine.jareditor;

import me.lowedermine.jareditor.jar.infos.ClassInfo;
import me.lowedermine.jareditor.jar.infos.PackageInfo;

public abstract class PreloadHub {
    public abstract ClassInfo[] getPreloads();
    public PackageInfo[] getExceptions() {
        return null;
    }
}
