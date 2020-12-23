package de.nkilders.win.hotspotadapter;

import java.util.ArrayList;
import java.util.List;

/**
 * An Adapter for Windows' Hosted Network (Hotspot)
 *
 * @author Noah Kilders
 */
public class HotspotAdapter {

    /**
     * Start the Hosted Network (Requires Admin)
     */
    public static void startHostedNetwork() {
        Cmd.exec("netsh wlan start hostednetwork");
    }

    /**
     * Stop the Hosted Network
     */
    public static void stopHostedNetwork() {
        Cmd.exec("netsh wlan stop hostednetwork");
    }

    /**
     * @return {@code true} if the Hosted Network is running
     */
    public static boolean isRunning() {
        return Cmd.exec("netsh wlan show hostednetwork")
                .split("\n").length > 11;
    }

    /**
     * Set the Hosted Network's SSID and password (Requires Admin)
     *
     * @param ssid     new SSID
     * @param password new password
     * @return commandline-output
     */
    public static String setHostedNetwork(String ssid, String password) {
        return Cmd.exec("netsh wlan set hostednetwork mode=\"allow\" key=\"%s\" key=\"%s\"", ssid, password);
    }

    /**
     * @return the Hosted Network's SSID
     */
    public static String getSSID() {
        String ssid = Cmd.exec("netsh wlan show hostednetwork")
                .split("\n")[3]
                .split(" : ", 2)[1];

        return ssid.substring(1, ssid.length() - 1);
    }

    /**
     * @return the Hosted Network's password
     */
    public static String getPassword() {
        return Cmd.exec("netsh wlan show hostednetwork setting=security")
                .split("\n")[5]
                .split(" : ", 2)[1];
    }

    /**
     * Sets a Registry key, which interface to use for hosting the Hosted Network.
     * Might help if the Hosted Network does not work properly.
     */
    public static void setPrivateInterface() {
        stopHostedNetwork();
        String[] arr1 = Cmd.exec("netsh int ipv4 show interfaces")
                .split("\n");
        startHostedNetwork();
        String[] arr2 = Cmd.exec("netsh int ipv4 show interfaces")
                .split("\n");

        for (int i = 2; i < arr1.length; i++) {
            String[] arr1Split = arr1[i].split(" ");
            String[] arr2Split = arr2[i].split(" ");

            if (!arr1Split[4].equals(arr2Split[4])) {
                Cmd.exec("reg add \"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SharedAccess\" /v PrivateIndex /t REG_DWORD /d %s /f", Integer.parseInt(arr1Split[1], 16));
                break;
            }
        }
    }

    /**
     * (Requires Admin)
     *
     * @param ipv4Interface IPv4-interface to share the Internet connection from
     */
    public static void setPublicInterface(IPv4Interface ipv4Interface) {
        if (ipv4Interface != null) {
            Cmd.exec("reg add \"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SharedAccess\" /v PublicIndex /t REG_DWORD /d %s /f",
                    Integer.parseInt("" + ipv4Interface.index, 16));
        }
    }

    /**
     * @return IPv4-interface from which the Internet connection is shared
     */
    public static IPv4Interface getPublicInterface() {
        String registryPublicIndex = Cmd.exec("reg query \"HKLM\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\SharedAccess\" /v PublicIndex")
                .split("\n")[1]
                .split(" ")[3]
                .substring(2);
        int publicIndex = registryPublicIndex.equals("ffffffff") ? -1 : Integer.parseInt(registryPublicIndex, 16);

        startHostedNetwork();
        String[] lines = Cmd.exec("netsh int ipv4 show interfaces")
                .split("\n");

        for (int i = 2; i < lines.length; i++) {
            if (lines[i].contains("Loopback Pseudo-Interface"))
                continue;

            String[] line = lines[i].split(" ", 6);
            if (Integer.parseInt(line[1]) == publicIndex) {
                return new IPv4Interface(Integer.parseInt(line[1]), line[5]);
            }
        }

        return null;
    }

    /**
     * @return an {@link java.util.ArrayList} of available IPv4-interfaces
     */
    public static ArrayList<IPv4Interface> getIPv4Interfaces() {
        startHostedNetwork();
        String[] lines = Cmd.exec("netsh int ipv4 show interfaces")
                .split("\n");

        ArrayList<IPv4Interface> interfaceList = new ArrayList<>();
        for (int i = 2; i < lines.length; i++) {
            if (lines[i].contains("Loopback Pseudo-Interface"))
                continue;

            String[] line = lines[i].split(" ", 6);
            if (line[4].equals("connected")) {
                interfaceList.add(new IPv4Interface(Integer.parseInt(line[1]), line[5]));
            }
        }

        return interfaceList;
    }

    /**
     * @return an {@link java.util.ArrayList} of devices (MAC-addresses) connected to the Hosted Network
     */
    public static ArrayList<String> getConnectedDevices() {
        ArrayList<String> deviceList = new ArrayList<>();
        String[] lines = Cmd.exec("netsh wlan show hostednetwork")
                .split("\n");

        if (lines.length > 15) {
            for (int i = 15; i < lines.length; i++) {
                deviceList.add(lines[i].split(" ")[1]);
            }
        }

        return deviceList;
    }
}