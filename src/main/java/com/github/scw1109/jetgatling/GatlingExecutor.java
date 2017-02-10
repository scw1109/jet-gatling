package com.github.scw1109.jetgatling;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import org.slf4j.LoggerFactory;

/**
 * @author scw1109
 */
public class GatlingExecutor {

    private static final String FIX_RPS_SIMULATION = "com.github.scw1109.jetgatling.simulations.FixRpsSimulation";

    public static int execute(JetGatlingOptions options) {
        String workingDir = System.getProperty("user.dir");

        GatlingPropertiesBuilder builder = new GatlingPropertiesBuilder();
        builder.simulationClass(FIX_RPS_SIMULATION);
        builder.dataDirectory(workingDir);
        builder.bodiesDirectory(workingDir);
        builder.mute();

        if (options.getTimeout() > -1) {
            System.setProperty("gatling.http.ahc.readTimeout", String.valueOf(options.getTimeout()));
        }

        if (options.isDebug() || options.isTrace()) {
            Level logLevel = options.isTrace() ? Level.TRACE : Level.DEBUG;

            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.getLogger("io.gatling.http.ahc").setLevel(logLevel);
            loggerContext.getLogger("io.gatling.http.response").setLevel(logLevel);
            loggerContext.getLogger("com.github.scw1109.jetgatling").setLevel(logLevel);
        }

        return Gatling.fromMap(builder.build());
    }
}
