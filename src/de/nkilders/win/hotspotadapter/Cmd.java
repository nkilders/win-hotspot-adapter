package de.nkilders.win.hotspotadapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Noah Kilders
 */
public class Cmd {

    /**
     * Execute system commands
     *
     * @param command Command to be executed
     * @return Commandline-output
     */
    public static String exec(String command) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append((sb.length() == 0 ? "" : "\n"))
                        .append(line.replaceAll("( )+", " "));
            }

            return sb.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
    }

    /**
     * The same method as {@link de.nkilders.win.hotspotadapter.Cmd#exec(String)} but with integrated {@link java.lang.String#format(String, Object...)}
     *
     * @param command Command to be executed
     * @param args    Formatting-arguments
     * @return Commandline-output
     */
    public static String exec(String command, Object... args) {
        return exec(String.format(command, args));
    }
}