package com.github.scw1109.jetgatling;

import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author scw1109
 */
public class JetGatlingOptions {

    private transient Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Parameter(names = {"-u", "--url"},
            description = "Base url (or target url if --paths is not provided), in format of [http[s]://]hostname[:port][/path]",
            required = true)
    private String baseUrl = "";

    @Parameter(names = {"-p", "--paths"},
            description = "A tsv file contains the target paths. Will circular through the file and append to base url.")
    private String pathFile = "";

    @Parameter(names = {"-r", "--rps"},
            description = "Number of RPS (request per second). Cannot be use as the same time of '-c'")
    private int rps = 0;

    @Parameter(names = {"-c", "--concurrency"},
            description = "Number of concurrent request to perform at a time. Cannot be use as the same time of '-r'")
    private int concurrency = 0;

    @Parameter(names = {"-d", "--duration"},
            description = "Duration (in second) for test to run")
    private int durationInSecond = 0;

    @Parameter(names = {"-R", "--ramp"},
            description = "Setup ramp up period to gracefully adding load to the target.")
    private int rampDurationInSecond = 0;

    @Parameter(names = {"-s", "--timeout"},
            description = "Maximum number of millisecond to wait before timeout for each request. " +
                    "Negative number means using Gatling default value.")
    private int timeout = -1;

    @Parameter(names = {"-m", "--http-method"},
            description = "Custom HTTP method for the request")
    private String httpMethod = "GET";

    @Parameter(names = {"-b", "--body"},
            description = "A file contains body data of POST/PUT. Remember to also set 'Content-Type' header using '-h'.")
    private String bodyFile = "";

    @Parameter(names = {"-H", "--header"},
            description = "Extra headers to the request. The argument is typically in the form of a valid header line, " +
                    "containing a colon-separated field-value pair (i.e., \"Accept-Encoding: zip/zop;8bit\"). This field is repeatable.")
    private List<String> headers = new ArrayList<>();

    @Parameter(names = {"-a", "--agent"},
            description = "Set the user agent header.")
    private String userAgent = "Gatling";

    @Parameter(names = {"-k", "--keep-alive"},
            description = "Enable the HTTP KeepAlive feature.")
    private boolean keepAlive = false;

    @Parameter(names = {"-D", "--debug"},
            description = "Enable debug mode, logging failed request and responses")
    private boolean debug = false;

    @Parameter(names = {"-V", "--verbose", "--trace"},
            description = "Enable trace mode, logging ALL request and responses")
    private boolean trace = false;

    @Parameter(names = {"-h", "--help"},
            description = "Display usage information",
            help = true)
    private boolean help = false;

    boolean isDebug() {
        return debug;
    }

    boolean isTrace() {
        return trace;
    }

    boolean isHelp() {
        return help;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPathFile() {
        return pathFile;
    }

    public int getRps() {
        return rps;
    }

    public int getDurationInSecond() {
        return durationInSecond;
    }

    public int getRampDurationInSecond() {
        return rampDurationInSecond;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public String getBodyFile() {
        return bodyFile;
    }

    public int getConcurrency() {
        return concurrency;
    }

    boolean check() {
        if (concurrency != 0 && rps != 0) {
            logger.error("Concurrency option cannot use together with RPS.");
            return false;
        }

        return true;
    }
}
