package me.lowdy.testing.control;

import me.lowedermine.jareditor.editing.modifier.ClassModifier;
import me.lowedermine.jareditor.editing.modifier.FieldModifier;
import me.lowedermine.jareditor.editing.representation.ClassRepresentation;
import me.lowedermine.jareditor.editing.representation.FieldRepresentation;

@ClassModifier()
@ClassRepresentation(value = "Executable", pkg = {"me", "lowdy", "testing"})
public class TestEdit {
    @FieldModifier(value = FieldModifier.Mode.EDIT, target = "anField")
    @FieldRepresentation("anField")
    public String someField;
}
