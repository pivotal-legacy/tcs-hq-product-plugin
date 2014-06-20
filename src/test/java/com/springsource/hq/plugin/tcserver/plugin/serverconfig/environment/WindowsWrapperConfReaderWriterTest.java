// Copyright (c) 2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;
import org.junit.Assert;
import org.junit.Test;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;

/**
 * TCS-94 reported an issue with repeated parsings of wrapper.conf on Windows causing insertion of blank lines before
 * the wrapper.java.additional section and removal of lines after that section, which would eventually destroy
 * configuration settings further down in the file.
 * 
 * This test case exposes the bug, verifies that even though it was Windows specific, the bug happens regardless of the
 * EOL character in the file. It demonstrates that the fix works by parsing the same file twice, and seeing that there
 * is no change between first parsing and second parsing.
 * 
 * @author Greg Turnquist
 * 
 */
public class WindowsWrapperConfReaderWriterTest {

    private final static String PREFIX = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "com" + File.separator
        + "springsource" + File.separator + "hq" + File.separator + "plugin" + File.separator + "tcserver" + File.separator + "plugin"
        + File.separator + "serverconfig" + File.separator + "environment";

    private final static String unixWrapperFilename = PREFIX + File.separator + "wrapper_in_unix_format.conf";

    private final static String windowsWrapperFilename = PREFIX + File.separator + "wrapper_in_windows_format.conf";

    /**
     * Verify the Windows parser works on files with UNIX EOL characters.
     * 
     * @throws Exception
     */
    @Test
    public void confirmBugIsFixedForUnixStyleFiles() throws Exception {
        runWrapperFileThroughParserTwiceAndConfirmNoChangeBetweenFirstAndSecondParsing(unixWrapperFilename, "unix");
    }

    /**
     * Verify the Windows parser works on files with Windows EOL characters.
     * 
     * @throws Exception
     */
    @Test
    public void confirmBugIsFixedForWindowStyleFiles() throws Exception {
        runWrapperFileThroughParserTwiceAndConfirmNoChangeBetweenFirstAndSecondParsing(windowsWrapperFilename, "windows");
    }

    /**
     * Copy the file through the API twice, and then compare the first parsed version with the second parsed version,
     * asserting there are no changes.
     */
    private void runWrapperFileThroughParserTwiceAndConfirmNoChangeBetweenFirstAndSecondParsing(String filename, String suffix) throws Exception {
        File tmpConfDir = parseAndSave(filename);

        String savedWrapperFilename = tmpConfDir + File.separator + "wrapper.conf";

        String parsedOnceFilename = PREFIX + File.separator + "parsed_once_wrapped_" + suffix + ".conf";
        copy(savedWrapperFilename, parsedOnceFilename);

        tmpConfDir = parseAndSave(parsedOnceFilename);

        String parsedSecondFilename = PREFIX + File.separator + "parsed_second_wrapped_" + suffix + ".conf";
        copy(savedWrapperFilename, parsedSecondFilename);

        /**
         * Confirm that first pased file and second parsed file are equal.
         */

        /*
         * Check line counts
         */
        final List<String> parsedOnceLines = readLines(parsedOnceFilename);
        final List<String> parsedTwiceLines = readLines(parsedSecondFilename);

        Assert.assertEquals(parsedOnceLines.size(), parsedTwiceLines.size());

        for (int i = 0; i < parsedOnceLines.size(); i++) {
            Assert.assertEquals(parsedOnceLines.get(i), parsedTwiceLines.get(i));
        }

        /*
         * Check character counts
         */
        final List<Integer> originalChars = readCharacters(parsedOnceFilename);
        final List<Integer> resultChars = readCharacters(parsedSecondFilename);

        Assert.assertEquals(originalChars.size(), resultChars.size());
    }

    /**
     * This method stages the file as <TMP>/conf/wrapper.conf and <TMP>/conf/wrapper.conf.bak, and then invokes the
     * {WindowsFileEnvironmentRepository}'s save operation to read/write it.
     * 
     * @param filename - source file that is to be parsed
     * @return handle on <TMP>/conf directory, so test methods can copy and interrogate the results
     * @throws IOException
     * @throws PluginException
     */
    private File parseAndSave(String filename) throws IOException, PluginException {
        final String confDirectory = System.getProperty("java.io.tmpdir") + File.separator + "conf";
        final File tmpConfDir = new File(confDirectory);
        tmpConfDir.mkdir();

        String wrapperFilename = tmpConfDir.toString() + File.separator + "wrapper.conf";
        copy(filename, wrapperFilename);

        String backupFileName = tmpConfDir.toString() + File.separator + "wrapper.conf.bak";
        copy(filename, backupFileName);

        ConfigResponse config = new ConfigResponse();
        config.setValue("installpath", System.getProperty("java.io.tmpdir"));

        /**
         * The following settings aren't critical, since the same file gets parsed twice. They are required by the API.
         * They demonstrate that values get inserted, but shouldn't vary between the first parsing and the second
         * parsing.
         */
        Environment newEnvironment = new Environment();
        newEnvironment.setJavaHome("c:\\path\\to\\java");
        newEnvironment.getJvmOptions().getMemory().setMx(1L);
        newEnvironment.getJvmOptions().getMemory().setMs(2L);
        newEnvironment.getJvmOptions().getMemory().setSs(3L);

        WindowsFileEnvironmentRepository repository = new WindowsFileEnvironmentRepository();
        repository.save(backupFileName, config, newEnvironment);

        return tmpConfDir;
    }

    /**
     * This method parses a text file into a list of characters (represented as int's).
     * 
     * @param filename
     * @return List<Integer> containing list of characters (including EOL character)
     * @throws IOException
     */
    private List<Integer> readCharacters(String filename) throws IOException {
        File file = new File(filename);
        final FileReader fileReader = new FileReader(file);

        final List<Integer> chars = new ArrayList<Integer>();

        int thisChar;
        while ((thisChar = fileReader.read()) != -1) {
            chars.add(thisChar);
        }

        return chars;
    }

    /**
     * This method parses a text file into a List<String> so support checking contents.
     * 
     * @param filename
     * @return List<String> containing list of lines (with EOL char stripped out)
     * @throws IOException
     */
    private List<String> readLines(String filename) throws IOException {
        File file = new File(filename);
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        final List<String> lines = new ArrayList<String>();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        return lines;

    }

    /**
     * This method is a convenient utility to copy files from one path to another.
     * 
     * @param inputFilename
     * @param outputFilename
     * @throws IOException
     */
    private void copy(String inputFilename, String outputFilename) throws IOException {
        InputStream in = new FileInputStream(inputFilename);
        OutputStream out = new FileOutputStream(outputFilename);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
