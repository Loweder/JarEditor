package me.lowdy.testing.control;

import me.lowedermine.jareditor.IPreloadHub;

public class TestHub implements IPreloadHub {
    @Override
    public String[] getPreloads() {
        return new String[] {"me.lowdy.testing.control.TestRenamer"};
    }

    @Override
    public String[] getExceptions() {
        return new String[] {"me.lowdy.testing.control."};
    }
}
