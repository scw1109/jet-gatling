package com.github.scw1109.jetgatling;

import com.beust.jcommander.JCommander;

/**
 * @author scw
 */
public class JetGatling {

    public static JetGatlingOptions OPTIONS;

    public static void main(String[] args) {
        JetGatlingOptions options = new JetGatlingOptions();
        JCommander jcommander = new JCommander(options, args);

        if (args.length == 0 || options.isHelp()) {
            jcommander.usage();
            System.exit(0);
        }

        OPTIONS = options;
        int result = GatlingExecutor.execute(options);
        System.exit(result);
    }
}
