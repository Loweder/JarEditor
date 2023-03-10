package me.lowedermine.jareditor.editing.preloads;

import java.util.Map;

public abstract class NameEditor<R, A> implements IEditor {
    protected abstract void setMappings(Map<R, A> mappings);

    public abstract A map(R name);

    public abstract A unmap(R name);
}
