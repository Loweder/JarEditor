package me.lowdy.testing.control;

import me.lowedermine.jareditor.EditingClassloader;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class TestIT {
    @Test
    void test1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EditingClassloader loader = new EditingClassloader(TestIT.class.getClassLoader());
        Class<?> found = loader.loadClass("me.lowdy.testing.Executable");
        found.getMethod("report").invoke(null);
    }

    @Test
    void test2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        System.setProperty("bundlerRepoDir", "server/bundler");
        EditingClassloader loader = new EditingClassloader(TestIT.class.getClassLoader());
        Class<?> found = loader.loadClass("org.bukkit.craftbukkit.bootstrap.Main");
        found.getMethod("main", String[].class).invoke(null, (Object) new String[] {"-nogui"});
        Thread[] t = Thread.getAllStackTraces().keySet().toArray(new Thread[0]);
        for (Thread thread : t) {
            if (thread.getName().equals("ServerMain")) thread.join();
        }
    }
}
