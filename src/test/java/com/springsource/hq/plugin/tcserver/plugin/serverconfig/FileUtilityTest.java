// Copyright (c) 2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilityTest {

    private final FileUtility fileUtility;

    private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static final String FILE1_TEXT = "tempfile1.txt text";

    private static final String FILE2_TEXT = "tempfile2.txt text";

    private static final Log LOGGER = LogFactory.getLog(FileUtilityTest.class);

    public FileUtilityTest() {
        fileUtility = new FileUtility();
    }

    @Before
    public void setup() throws IOException {
        LOGGER.debug("TEMP_DIR = " + TEMP_DIR);
        File sampleDirectory = new File(TEMP_DIR + "/config");
        LOGGER.debug("Temp dir/config created: " + sampleDirectory.mkdir());
        File sampleFile1 = new File(sampleDirectory.getAbsolutePath() + "/tempfile1.tmp");
        sampleFile1.createNewFile();
        LOGGER.debug("SampleFile1 - " + sampleFile1.getAbsolutePath() + " - created: " + sampleFile1.isFile());
        FileWriter fileWriter = new FileWriter(sampleFile1);
        fileWriter.append(FILE1_TEXT);
        fileWriter.close();
        File sampleFile2 = new File(sampleDirectory.getAbsolutePath() + "/tempfile2.tmp");
        sampleFile2.createNewFile();
        LOGGER.debug("SampleFile2 - " + sampleFile2.getAbsolutePath() + " - created: " + sampleFile1.isFile());
        fileWriter = new FileWriter(sampleFile2);
        fileWriter.append(FILE2_TEXT);
        fileWriter.close();
    }

    @After
    public void cleanUp() {
        File backupDirectory = new File(TEMP_DIR + "/backup");
        recursiveFileAndDirectoryDelete(backupDirectory);
        File configDirectory = new File(TEMP_DIR + "/config");
        recursiveFileAndDirectoryDelete(configDirectory);
    }

    private void recursiveFileAndDirectoryDelete(File file) {
        File[] fileListing = file.listFiles();
        for (int index = 0; index < fileListing.length; index++) {
            File subFile = fileListing[index];
            if (subFile.isDirectory()) {
                recursiveFileAndDirectoryDelete(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    @Test
    public void testCopyBackupFile() throws IOException {
        String dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String separator = System.getProperty("file.separator");

        String fakeFileName = "config" + separator + "tempfile1.tmp";
        fileUtility.copyBackupFile(TEMP_DIR, fakeFileName);

        File fakeFileTest = new File(TEMP_DIR + separator + fakeFileName);
        assertTrue("The fake file (" + fakeFileTest.getPath() + ") must exist on the filesystem", fakeFileTest.isFile());

        StringBuilder fileName = new StringBuilder(TEMP_DIR);
        fileName.append(separator).append("backup").append(separator).append(dateFormat).append(separator).append("config").append(separator).append(
            "tempfile1.tmp");

        File backup = new File(fileName.toString());
        assertTrue("The backup file should exist", backup.isFile());
    }

    @Test
    public void testGetLatestBackedupDirectory() throws IOException, InterruptedException {
        fileUtility.copyBackupFile(TEMP_DIR, "config", "tempfile1.tmp", new Date());
        // sleep to create a new directory (since it is based off of seconds)
        Thread.sleep(2000);
        Date latestDate = new Date();
        String expectedDirectoryString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(latestDate);
        fileUtility.copyBackupFile(TEMP_DIR, "config", "tempfile2.tmp", latestDate);
        Set<String> latestDirs = fileUtility.getLatestBackupDirectories(TEMP_DIR);
        String highestDirectory = ((TreeSet<String>) latestDirs).last();
        assertEquals("The directories should be equal.", expectedDirectoryString, highestDirectory);
    }

    @Test
    public void testRevertLatestBackupFiles() throws IOException, InterruptedException, FileUtilityException {
        LOGGER.debug("REVERT TEST STARTING");
        Date latestDate = new Date();
        fileUtility.copyBackupFile(TEMP_DIR, "config", "tempfile2.tmp", latestDate);
        File alterFile = new File(TEMP_DIR + "/config/tempfile2.tmp");
        LOGGER.debug("ALTER FILE - " + alterFile.getAbsolutePath() + " - Exists: " + alterFile.isFile());
        FileWriter writer = new FileWriter(alterFile);
        writer.append("THIS IS A NEWER FILE");
        writer.close();
        FileReader reader = new FileReader(alterFile);
        BufferedReader br = new BufferedReader(reader);
        String fileText = br.readLine();
        reader.close();
        assertEquals("This text should be the same.", "THIS IS A NEWER FILE", fileText);
        Set<String> latestDirs = fileUtility.getLatestBackupDirectories(TEMP_DIR);
        String highestDirectory = ((TreeSet<String>) latestDirs).last();
        fileUtility.revertToBackupFiles(TEMP_DIR, highestDirectory);
        alterFile = new File(TEMP_DIR + "/config/tempfile2.tmp");
        LOGGER.debug("ALTER FILE After - " + alterFile.getAbsolutePath() + " - Exists: " + alterFile.isFile());
        reader = new FileReader(alterFile);
        br = new BufferedReader(reader);
        fileText = br.readLine();
        reader.close();
        assertEquals("The text in the file should be the same.", FILE2_TEXT, fileText);
    }
}
