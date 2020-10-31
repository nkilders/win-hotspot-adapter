package de.noah.win.hotspotadapter;

/**
 * @author Noah Kilders
 */
public class IPv4Interface {
    final int index;
    final String name;

    public IPv4Interface(int index, String name) {
        this.index = index;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
