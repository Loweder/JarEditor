package me.lowdy.testing;

import me.lowdy.testing.data.DummedString;
import me.lowdy.testing.data.Invisibil;
import me.lowdy.testing.data.Visibil;

@Visibil(value = 10, str = "Value", enumer = Visibil.AnEnum.B, array = {10, 15, 255}, clazz = DummedString.class)
@Invisibil(value = 94, str = "StrRules", enumer = Invisibil.AnEnum.C, array = {57}, clazz = Void.class)
public class Executable {

    public static final String anField = "anField";

    public static void report() {
        System.out.println("My, " + Executable.class.getName() + " classloader is " + Executable.class.getClassLoader().getClass().getName());
        SignatureTests.trigger();
        OtherTest.mtd();
    }
}
