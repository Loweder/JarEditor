package me.lowdy.testing;

public class OtherTest {
    public static void mtd() {
        System.out.println("OtherTest (" + OtherTest.class.getName() + ") classloader is " + OtherTest.class.getClassLoader().getClass().getName());
    }
}
