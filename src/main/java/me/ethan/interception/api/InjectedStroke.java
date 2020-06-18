package me.ethan.interception.api;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class InjectedStroke extends Structure {
    public short code;
    public short state;
    public int information;
    public short rolling;
    public int x;
    public int y;
    public short flags;
    private boolean injected;

    public InjectedStroke() {
        injected = false;
    }

    protected List getFieldOrder() {
        return Arrays.asList(new String[] { "code", "state", "flags", "rolling", "x", "y", "information"});
    }

    public InjectedStroke(short code, short state, short flags, short rolling, int x, int y, int information, boolean injected) {
        this.code = code;
        this.state = state;
        this.flags = flags;
        this.rolling = rolling;
        this.x = x;
        this.y = y;
        this.information = information;
        this.injected = injected;
    }

    public boolean isInjected() {
        return injected;
    }
}
