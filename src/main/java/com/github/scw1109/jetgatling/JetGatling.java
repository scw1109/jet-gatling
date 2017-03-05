package com.github.scw1109.jetgatling;

import com.beust.jcommander.JCommander;

/**
 * @author scw1109
 */
public class JetGatling {

    public static JetGatlingOptions OPTIONS;

    public static void main(String[] args) {
        int returnCode = startGatling(args);
        System.exit(returnCode);
    }

    private static int startGatling(String[] args) {
        JetGatlingOptions options = new JetGatlingOptions();
        JCommander jcommander = new JCommander(options, args);

        if (args.length == 0 || options.isHelp()) {
            jcommander.usage();
            return 0;
        }

        if (!options.check()) {
            return 10;
        }

        OPTIONS = options;
        return GatlingExecutor.execute(options);
    }
}
