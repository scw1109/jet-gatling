package com.github.scw1109.jetgatling;

import com.xebialabs.restito.semantics.Action;
import com.xebialabs.restito.semantics.Call;
import com.xebialabs.restito.semantics.Condition;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.uri;
import static com.xebialabs.restito.semantics.Condition.withHeader;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author scw1109
 */
public class JetGatlingTest {

    private StubServer stubServer;

    @Before
    public void start() {
        stubServer = new StubServer().run();
    }

    @After
    public void stop() {
        stubServer.clear();
        stubServer.stop();
    }

    @Test
    public void testRps() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(ok());

        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort()
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // 6 gatling load request
        verifyHttp(stubServer).times(1 + 6,
                method(Method.GET),
                uri("/")
        );
    }

    @Test
    public void testRps_slow() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(Action.custom(input -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return input;
                }), ok());

        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "5",
                "-u", "http://localhost:" + stubServer.getPort()
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // 10 gatling load request
        verifyHttp(stubServer).times(1 + 10,
                method(Method.GET),
                uri("/")
        );
    }

    @Test
    public void testRps_timeout() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(Action.custom(input -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return input;
                }), ok());

        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "5",
                "-s", "1000",
                "-u", "http://localhost:" + stubServer.getPort()
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // 10 gatling load request
        verifyHttp(stubServer).times(1 + 10,
                method(Method.GET),
                uri("/")
        );
    }

    @Test
    public void testConcurrent() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(Action.custom(input -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return input;
                }), ok());

        int returnCode = runGatling(new String[]{
                "-c", "2",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort() + "/"
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // ~30 gatling load request
        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.GET),
                uri("/"));
        assertTrue(calls.size() > 25 && calls.size() < 35);
    }

    @Test
    public void testConcurrent_slow() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(Action.custom(input -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return input;
                }), ok());

        int returnCode = runGatling(new String[]{
                "-c", "2",
                "-d", "6",
                "-u", "http://localhost:" + stubServer.getPort() + "/"
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // ~8 gatling load request
        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.GET),
                uri("/"));
        assertTrue(calls.size() > 6 && calls.size() < 10);
    }

    @Test
    public void testConcurrent_timeout() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(Action.custom(input -> {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return input;
                }), ok());

        int returnCode = runGatling(new String[]{
                "-c", "2",
                "-d", "6",
                "-s", "1000",
                "-u", "http://localhost:" + stubServer.getPort() + "/"
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // ~12 gatling load request
        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.GET),
                uri("/"));
        assertTrue(calls.size() > 10 && calls.size() < 14);
    }

    @Test
    public void testRps_withPathFile() {
        // For baseUrl automatic warm-up
        whenHttp(stubServer)
                .match(get("/"))
                .then(ok());

        // For the two urls in path file
        whenHttp(stubServer)
                .match(get("/a"))
                .then(ok());
        whenHttp(stubServer)
                .match(get("/b"))
                .then(ok());

        URL url = getClass().getClassLoader().getResource("path.txt");
        if (url == null) {
            fail("Cannot find path file.");
        }
        String path = url.getPath();

        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "6",
                "-u", "http://localhost:" + stubServer.getPort(),
                "-p", path
        });

        assertEquals(0, returnCode);

        verifyHttp(stubServer).times(1,
                method(Method.GET),
                uri("/")
        );
        verifyHttp(stubServer).times(6,
                method(Method.GET),
                uri("/a")
        );
        verifyHttp(stubServer).times(6,
                method(Method.GET),
                uri("/b")
        );
    }

    @Test
    public void testRps_post() {
        // For baseUrl automatic warm-up
        whenHttp(stubServer)
                .match(get("/"))
                .then(ok());

        // For the post urls
        whenHttp(stubServer)
                .match(post("/"))
                .then(ok());

        URL url = getClass().getClassLoader().getResource("body.txt");
        if (url == null) {
            fail("Cannot find path file.");
        }
        String bodyFile = url.getPath();

        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort(),
                "-m", "POST",
                "-b", bodyFile,
                "-H", "Content-Type: text/plain"
        });

        assertEquals(0, returnCode);

        verifyHttp(stubServer).times(1,
                method(Method.GET),
                uri("/")
        );
        verifyHttp(stubServer).times(6,
                method(Method.POST),
                uri("/")
        );

        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.POST),
                uri("/"));
        Call call = calls.get(0);
        assertEquals("text/plain", call.getContentType());
        assertEquals("POST body", call.getPostBody());
    }

    @Test
    public void testRps_headers() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(ok());


        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort(),
                "-a", "Fake User agent"
        });

        assertEquals(0, returnCode);

        verifyHttp(stubServer).times(1 + 6,
                method(Method.GET),
                uri("/")
        );

        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.GET),
                uri("/"),
                withHeader("user-agent", "Fake User agent"));
        Call call = calls.get(0);
        Map<String, String> headers = call.getHeaders();

        assertEquals("Fake User agent", headers.get("user-agent"));
        assertEquals("close", headers.get("connection"));
    }

    @Test
    public void testRps_keepAlive() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(ok());


        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort(),
                "-k"
        });

        assertEquals(0, returnCode);

        verifyHttp(stubServer).times(1 + 6,
                method(Method.GET),
                uri("/")
        );

        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.GET),
                uri("/"),
                withHeader("connection", "keep-alive"));
        Call call = calls.get(0);
        Map<String, String> headers = call.getHeaders();

        assertEquals("keep-alive", headers.get("connection"));
    }

    @Test
    public void testRps_withRamp() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(ok());

        int returnCode = runGatling(new String[]{
                "-r", "2",
                "-R", "5",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort()
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // 5+ ramp up request
        // 6 gatling load request
        verifyHttp(stubServer).atLeast(1 + 5 + 6,
                method(Method.GET),
                uri("/")
        );
    }

    @Test
    public void testConcurrent_withRamp() {
        whenHttp(stubServer)
                .match(get("/"))
                .then(Action.custom(input -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return input;
                }), ok());

        int returnCode = runGatling(new String[]{
                "-c", "2",
                "-R", "5",
                "-d", "3",
                "-u", "http://localhost:" + stubServer.getPort()
        });

        assertEquals(0, returnCode);

        // 1 automatic warm-up request
        // ~25 ramp up request
        // ~30 gatling load request
        List<Call> calls = filterByConditions(stubServer.getCalls(),
                method(Method.GET),
                uri("/"));
        assertTrue(calls.size() > 50 && calls.size() < 60);
    }

    private List<Call> filterByConditions(List<Call> calls, Condition... conditions) {
        List<Call> filteredCalls = new ArrayList<>(calls);

        for (Condition condition : conditions) {
            filteredCalls.removeIf(call -> !condition.getPredicate().test(call));
        }
        return filteredCalls;
    }

    /**
     * Running Gatling multiple times in same JVM has problem.
     * Hence, spawning another JVM for the run.
     */
    private int runGatling(String[] args) {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String javaCommand = System.getProperty("java.home")
                + separator + "bin" + separator + "java";

        List<String> commands = new ArrayList<>(20);
        commands.add(javaCommand);
        commands.add("-cp");
        commands.add(classpath);
        commands.add(JetGatling.class.getName());
        commands.addAll(Arrays.asList(args));

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.inheritIO();

        Process process;
        try {
            process = processBuilder.start();
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
