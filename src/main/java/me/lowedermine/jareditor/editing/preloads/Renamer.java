package me.lowedermine.jareditor.editing.preloads;

import java.util.List;
import java.util.Map;

public abstract class Renamer<R, A> implements IEditor {
    protected abstract void setMappings(Map<R, A> mappings);

    public abstract A map(R name);

    public abstract A unmap(R name);

    @Override
    public List<String> getEdited() {
        return null;
    }
}
