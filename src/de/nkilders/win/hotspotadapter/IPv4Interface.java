package de.nkilders.win.hotspotadapter;

/**
 * @author Noah Kilders
 */
public class IPv4Interface {
    private final int index;
    private final String name;

    public IPv4Interface(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "IPv4Interface{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}