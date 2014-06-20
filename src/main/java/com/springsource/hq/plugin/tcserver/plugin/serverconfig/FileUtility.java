/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for handling the backing up and the restoring of the backed up files.
 * 
 * @author jasonkonicki
 * 
 */
public class FileUtility {

    private static final String BACKUP_SUB_DIR_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    private static final Log LOGGER = LogFactory.getLog(FileUtility.class);

    /**
     * Copies the file from the specified file path to a backup directory. The backup directory is created under the
     * baseDirectory (i.e. baseDirectory/backup/yyyy-MM-dd_HH-mm-ss/file-path). The date is reflective of the system
     * time when this is called.
     * 
     * @param baseDirectory The base directory to create the backup directory in and where the original files are
     *        located.
     * @param fromBaseDirFilePath The path to the files to be backed up.
     * @return The destination file
     * @throws IOException
     */
    public String copyBackupFile(final String baseDirectory, final String fromBaseDirFilePath) throws IOException {
        String fromBaseDir = "";
        String filePath = "";
        String p = fromBaseDirFilePath.replace("\\", "/");

        if (p != null && p.contains("/")) {
            int index = p.lastIndexOf("/");
            fromBaseDir = p.substring(0, index);
            filePath = p.substring(index + 1, p.length());
        }
        return copyBackupFile(baseDirectory, fromBaseDir, filePath, new Date());

    }

    /**
     * Copies the file from the specified file path to a backup directory. The backup directory is created under the
     * baseDirectory (i.e. baseDirectory/backup/yyyy-MM-dd_HH-mm-ss/file-path).
     * 
     * @param baseDirectory The base directory to create the backup directory in and where the original files are
     *        located.
     * @param sourceFileName The file name to be backed up.
     * @param fromBaseDir The directory path under the base directory where the files are located.
     * @param date The date the objects are backed up. Convenient for organizing all files in the same backup directory
     *        by time.
     * @return The destination file
     * @throws IOException
     */
    public String copyBackupFile(final String baseDirectory, final String fromBaseDir, final String sourceFileName, Date date) throws IOException {
        String separator = System.getProperty("file.separator");

        StringBuilder src = new StringBuilder(baseDirectory);
        src.append(separator).append(fromBaseDir).append(separator).append(sourceFileName);

        final String sourceFilePath = src.toString();

        StringBuilder dest = new StringBuilder(baseDirectory);
        dest.append(separator).append("backup").append(separator).append(new SimpleDateFormat(BACKUP_SUB_DIR_FORMAT).format(date)).append(separator).append(
            fromBaseDir);

        String destinationDirName = dest.toString();

        /*
         * Test to see if the backup dir can be created, if not create the backup file in the current directory.
         */
        File destinationDirectory = new File(destinationDirName);
        if (!destinationDirectory.isDirectory() && !destinationDirectory.mkdirs()) {
            LOGGER.warn("Unable to create directory '" + destinationDirName + "' for backup files." + " Using '" + baseDirectory + separator
                + fromBaseDir + "' directory instead.");

            destinationDirName = baseDirectory + separator + fromBaseDir;
        }

        String destinationFile = destinationDirName + separator + sourceFileName;

        LOGGER.debug("Backing up source file [" + sourceFilePath + "] to destination file [" + destinationFile + "], ");

        InputStream in = null;
        FileOutputStream out = null;

        try {
            final File source = new File(sourceFilePath);
            final File destination = new File(destinationFile);
            if (source.exists()) {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);
                copyFiles(in, out);
                out.flush();
                out.getFD().sync();
            } else {
                // the destination is null as there was no file to copy
                return null;
            }
            LOGGER.debug("DESTINATION FILE- " + destination.getAbsolutePath() + " -Exists: " + destination.isFile());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.warn("Error closing input stream from file: " + sourceFileName + ".  Cause: " + e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.warn("Error closing output stream to file: " + destinationFile + ".  Cause: " + e.getMessage());
                }
            }
        }
        return destinationFile;
    }

    /**
     * Copies the files from one file to another
     * 
     * @param in Source file inputstream
     * @param out Destination file outputstream
     * @throws IOException
     */
    public void copyFiles(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }

    /**
     * Retrieves the set of backup directories located in the base-directory/backup directory.
     * 
     * @param baseDirectory Used as the base to form the backup directory.
     * @return The set of backup directories (in the form yyyy-MM-dd_HH-mm-ss) located in the backup directory.
     */
    public Set<String> getLatestBackupDirectories(String baseDirectory) {
        Set<String> fileSet = new TreeSet<String>();
        File fileDirectory = new File(baseDirectory + "/backup");
        if (fileDirectory.isDirectory()) {
            File[] backupDirectories = fileDirectory.listFiles(new FileFilter() {

                public boolean accept(File file) {
                    return file.isDirectory();
                }
            });
            for (File directory : backupDirectories) {
                fileSet.add(directory.getName());
            }
        }
        return fileSet;
    }

    private void printArray(String[] array) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for (String element : array) {
            buffer.append(element + ", ");
        }
        buffer.append("]");
        buffer.substring(0, buffer.length() - 3);
        LOGGER.debug(buffer.toString());
    }

    private void recursiveFileCopy(String destinationDirectory, File source) throws IOException {
        LOGGER.debug("DestinationDirectory - " + destinationDirectory);
        LOGGER.debug("File Listing for " + source.getAbsolutePath() + " - ");
        printArray(source.list());
        for (File subFile : source.listFiles()) {
            if (subFile.isDirectory()) {
                File destinationDir = new File(((destinationDirectory.endsWith("/")) ? destinationDirectory : destinationDirectory + "/")
                    + subFile.getName());
                if (!destinationDir.isDirectory() && !destinationDir.mkdir()) {
                    throw new IOException(String.format("Unable to create directory '%s'", destinationDir));
                }
                recursiveFileCopy(destinationDir.getAbsolutePath(), subFile);
            } else {
                FileInputStream fileIn = null;
                FileOutputStream fileOut = null;
                try {
                    fileIn = new FileInputStream(subFile);
                    fileOut = new FileOutputStream(new File(destinationDirectory + "/" + subFile.getName()));
                    copyFiles(fileIn, fileOut);

                    fileOut.flush();
                    fileOut.getFD().sync();
                } finally {
                    if (fileIn != null) {
                        try {
                            fileIn.close();
                        } catch (Exception e) {
                            // Nothing to do
                        }
                    }
                    if (fileOut != null) {
                        try {
                            fileOut.close();
                        } catch (Exception e) {
                            // Nothing to do
                        }
                    }
                }
            }
        }
    }

    /**
     * Reverts the specified backupDirectory to its original location. For example: Assuming /usr/local/bin is the
     * baseDirectory. It will move /usr/local/bin/backup/yyyy-MM-dd_HH-mm-ss/* to /usr/local/bin
     * 
     * @param baseDirectory The base directory to find the backup directory
     * @param backupDirectoryName The backup directory in the form yyyy-MM-dd_HH-mm-ss to revert files.
     * @throws FileUtilityException
     */
    public void revertToBackupFiles(String baseDirectory, String backupDirectoryName) throws FileUtilityException {
        Set<String> allBackupDirectories = getLatestBackupDirectories(baseDirectory);
        if (allBackupDirectories.isEmpty()) {
            throw new FileUtilityException("Unable to revert to backup files: No backup directories found in " + baseDirectory + "/backup");
        }
        LOGGER.debug("ALLBACKUPS = " + allBackupDirectories);
        baseDirectory = baseDirectory.replace("\\", "/");
        for (String directoryName : allBackupDirectories) {
            if (directoryName.equals(backupDirectoryName)) {
                // find the backup dir
                String fileDirectoryString = ((baseDirectory.endsWith("/")) ? baseDirectory : baseDirectory + "/") + "backup/" + backupDirectoryName;
                File fileDirectory = new File(fileDirectoryString);
                LOGGER.debug("BackupDirectory - " + fileDirectory.getAbsolutePath() + " - Exists: " + fileDirectory.isDirectory());
                try {
                    recursiveFileCopy(baseDirectory, fileDirectory);
                } catch (IOException e) {
                    throw new FileUtilityException("Unable to revert backup files: " + e.getMessage(), e);
                }
            }
        }
    }
}
