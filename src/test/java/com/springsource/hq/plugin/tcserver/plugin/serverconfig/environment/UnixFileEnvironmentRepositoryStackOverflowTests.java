
package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

public class UnixFileEnvironmentRepositoryStackOverflowTests {

    private final UnixFileReadingEnvironmentFactory environmentFactory = new UnixFileReadingEnvironmentFactory();

    @Test
    public void failOnCyclicRefToJvmOpts() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        String folder = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/StackOverflowTests";
        config.setValue("installpath", Metric.encode(folder));
        Environment env = environmentFactory.create(config);
        assertEquals("/export/webapps/scommon/jdk/jdk1.6.0", env.getJavaHome());
    }

    @Test
    public void extractingJavaHome() throws IOException {
        String folder = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/StackOverflowTests";
        String file = "/bin/setenv.sh";
        Map<String, String[]> symbolTable = environmentFactory.extractPropertyMapping(folder + file);
        Set<String> values = new LinkedHashSet<String>();
        environmentFactory.expandValue(symbolTable, "JAVA_HOME", values);
        assertEquals("/export/webapps/scommon/jdk/jdk1.6.0", environmentFactory.collectionToSpaceDelimitedString(values));
    }

    @Test
    public void extractingSymbols() throws IOException {
        String folder = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/StackOverflowTests";
        String file = "/bin/setenv.sh";
        Map<String, String[]> symbolTable = environmentFactory.extractPropertyMapping(folder + file);

        assertTrue(symbolTable.containsKey("JAVA_HOME"));
        assertTrue(symbolTable.containsKey("JVM_OPTS"));
        assertTrue(symbolTable.containsKey("SAVE_JVM_OPTS"));

        String[] jvmOptions = symbolTable.get("JVM_OPTS");
        assertEquals(3, jvmOptions.length);
        assertTrue(jvmOptions[2].startsWith("$"));

        String[] saveJvmOptions = symbolTable.get("SAVE_JVM_OPTS");
        assertEquals(1, saveJvmOptions.length);
        assertTrue(saveJvmOptions[0].startsWith("$"));
        Set<String> values = new LinkedHashSet<String>();
        environmentFactory.expandValue(symbolTable, "NOT_THERE", values);
        assertTrue("Values should be empty", values.isEmpty());
        values.clear();
        environmentFactory.expandValue(symbolTable, "JAVA_HOME", values);
        assertEquals("/export/webapps/scommon/jdk/jdk1.6.0", environmentFactory.collectionToSpaceDelimitedString(values));
        values.clear();
        environmentFactory.expandValue(symbolTable, "JVM_OPTS", values);
        assertEquals("-Xmx512M -Xss192K", environmentFactory.collectionToSpaceDelimitedString(values));
    }

    @Test
    public void parsingTheNewWay() throws PluginException {
        ConfigResponse config = new ConfigResponse();
        String folder = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/StackOverflowTests";
        config.setValue("installpath", Metric.encode(folder));
        Environment env = environmentFactory.create(config);
        assertEquals("/export/webapps/scommon/jdk/jdk1.6.0", env.getJavaHome());
        assertEquals(512L, env.getJvmOptions().getMemory().getMx().longValue());
        assertEquals(192L, env.getJvmOptions().getMemory().getSs().longValue());
    }

    @Test
    public void removeWrappingQuotesSomeData() {
        assertEquals("data", environmentFactory.stripOutsideQuotesAndTrim("\"data\""));
    }

    @Test
    public void removeWrappingQuotesDataThatWasAlreadyDestringified() {
        assertEquals("data", environmentFactory.stripOutsideQuotesAndTrim("data"));
    }

    @Test
    public void removeWrappingQuotesAndGobsOfSuperfluousSpacing() {
        assertEquals("data", environmentFactory.stripOutsideQuotesAndTrim(" data "));
        assertEquals("data", environmentFactory.stripOutsideQuotesAndTrim("\" data \""));
    }

    @Test
    public void removeWrappingQuotesAroundSpaceSeparatedDataFields() {
        assertEquals("data1 data2", environmentFactory.stripOutsideQuotesAndTrim("  data1 data2   "));
    }

    @Test
    public void parsingMultipleLines() throws IOException {
        String folder = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/StackOverflowTestsWithWrappingLines";
        String file = "/bin/setenv.sh";
        Map<String, String[]> symbolTable = environmentFactory.extractPropertyMapping(folder + file);
        Set<String> values = new LinkedHashSet<String>();
        environmentFactory.expandValue(symbolTable, "MULTI_LINE", values);
        assertEquals("data1 data2", environmentFactory.collectionToSpaceDelimitedString(values));
        values.clear();
        environmentFactory.expandValue(symbolTable, "MULTI2", values);
        assertEquals("data1 data2 data3", environmentFactory.collectionToSpaceDelimitedString(values));
    }

    @Test
    public void parsingMultipleLinesWithAnError() throws IOException {
        String folder = "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/serverconfig/environment/StackOverflowTestsWithWrappingLinesWithError";
        String file = "/bin/setenv.sh";
        Map<String, String[]> symbolTable = environmentFactory.extractPropertyMapping(folder + file);
        Set<String> values = new LinkedHashSet<String>();
        environmentFactory.expandValue(symbolTable, "MULTI_LINE", values);
        assertEquals("data1 data2", environmentFactory.collectionToSpaceDelimitedString(values));
        values.clear();
        environmentFactory.expandValue(symbolTable, "MULTI2", values);
        // this test case is not what is intended but it is handling the mistake correctly.
        assertEquals("data1 data2 SAVE_JVM_OPTS=$JVM_OPTS", environmentFactory.collectionToSpaceDelimitedString(values));
        values.clear();
        environmentFactory.expandValue(symbolTable, "JAVA_HOME", values);
        assertEquals("/export/webapps/scommon/jdk/jdk1.6.0", environmentFactory.collectionToSpaceDelimitedString(values));
        values.clear();
        environmentFactory.expandValue(symbolTable, "JVM_OPTS", values);
        assertEquals("-Xmx512M -Xss192K", environmentFactory.collectionToSpaceDelimitedString(values));
    }
}
