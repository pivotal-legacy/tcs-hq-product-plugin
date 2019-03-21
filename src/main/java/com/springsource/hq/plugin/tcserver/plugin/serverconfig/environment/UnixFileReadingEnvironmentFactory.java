/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.PluginException;
import org.hyperic.util.config.ConfigResponse;

import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.Environment;
import com.springsource.hq.plugin.tcserver.serverconfig.configuration.jvm.JvmOptions;

public class UnixFileReadingEnvironmentFactory implements EnvironmentFactory {

    private final static Log log = LogFactory.getLog(UnixFileReadingEnvironmentFactory.class);

    private JvmOptionsConverter jvmOptionsConverter = new DefaultJvmOptionsConverter();

    /**
     * Scan the setenv.sh file one time, gathering a symbol table built out of "variable=value" lines. Then look for two
     * important values: JAVA_HOME and JVM_OPTS.
     */
    public Environment create(ConfigResponse config) throws PluginException {
        try {
            Environment environment = new Environment();
            if (new File(Metric.decode(config.getValue("installpath")) + "/bin/setenv.sh").exists()) {
                String envFile = Metric.decode(config.getValue("installpath")) + "/bin/setenv.sh";

                Map<String, String[]> symbolTable = extractPropertyMapping(envFile);
                Set<String> jvmOptsValues = new LinkedHashSet<String>();
                expandValue(symbolTable, "JVM_OPTS", jvmOptsValues);
                JvmOptions combJvmOptions = jvmOptionsConverter.convert(new ArrayList<String>(jvmOptsValues));
                environment.setJvmOptions(combJvmOptions);
                Set<String> javaHome = new LinkedHashSet<String>();
                expandValue(symbolTable, "JAVA_HOME", javaHome);

                environment.setJavaHome(collectionToSpaceDelimitedString(javaHome));
            }
            return environment;
        } catch (IOException e2) {
            for (StackTraceElement elem : e2.getStackTrace()) {
                log.info(elem.toString());
            }
            throw new PluginException("Unable to read existing tc Runtime configuration.  Cause: " + e2.getMessage());
        } catch (Exception e) {
            log.error("Received " + e.getMessage());
            e.printStackTrace();
            throw new PluginException("Unable to read existing tc Runtime configuration.  Cause: " + e.getMessage());
        }
    }

    protected String collectionToSpaceDelimitedString(Collection<String> collection) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String entry : collection) {
            stringBuilder.append(entry).append(" ");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    /**
     * Scan the file looking for "variable=value" lines. When found, split key/value based on the '=', strip out
     * superfluous quotes and whitespace from the 'value', and store in a map.
     * 
     * @param envFile
     * @return
     * @throws IOException
     */
    protected Map<String, String[]> extractPropertyMapping(String envFile) throws IOException {
        Map<String, String[]> propertyMap = new HashMap<String, String[]>();
        BufferedReader envFileReader = new BufferedReader(new FileReader(envFile));
        try {
            // These variables must be declared outside the loop, so that multi-line data can
            // be accrued across iterations of the file.
            String multiLineKey = null;
            String multiLineValue = null;

            for (String line; (line = envFileReader.readLine()) != null;) {

                // Handle situation where multi-line mode was entered on previous line, or we have accumulated more
                // multi-line content.
                if (multiLineValue != null) {
                    if (line.contains("\\")) {
                        multiLineValue += " " + line.replace("\\", "").trim();
                        continue;
                    } else {
                        // will we drop out of multi-line mode and store the key/value
                        multiLineValue += " " + line.trim();
                        addEachValueToMap(propertyMap, multiLineKey, multiLineValue);

                        // Now wipe the multi-line data, switching us back to regular mode.
                        multiLineKey = null;
                        multiLineValue = null;
                        continue;
                    }
                }

                if (line.contains("=")) {
                    final String key = line.substring(0, indexOfFirstEqualsSign(line)); // don't use String.split
                                                                                        // because the value may have an
                                                                                        // equals sign
                    final String value = line.substring(indexOfFirstEqualsSign(line) + 1);
                    log.debug("Reading key/value pair " + key + "/" + value);
                    if (!value.endsWith("\\")) { // Handle the simple case, where the value doesn't end with a
                                                 // multi-line '\' character
                        addEachValueToMap(propertyMap, key, value);
                    } else { // Now handle the complex case where you must capture the initial chunk of text and
                             // then
                             // switch to multi-line mode.
                        multiLineKey = key;
                        multiLineValue = value.substring(0, value.length() - 1).trim() + " ";
                        continue;
                    }
                }

            }
            return propertyMap;
        } finally {
            envFileReader.close();
        }
    }

    /**
     * @param line
     * @return
     */
    private int indexOfFirstEqualsSign(String line) {
        return line.indexOf("=");
    }

    /**
     * @param propertyMap
     * @param key
     * @param value
     */
    private void addEachValueToMap(Map<String, String[]> propertyMap, final String key, final String value) {
        String strippedValue = stripOutsideQuotesAndTrim(value);
        String[] splitValueBySpace = strippedValue.split("\\s+");
        for (String splitValue : splitValueBySpace) {
            if (propertyMap.containsKey(key)) {
                appendNewValueToExistingEntry(propertyMap, key, splitValue);
            } else {
                propertyMap.put(key, new String[] { splitValue });
            }
        }
    }

    protected String stripOutsideQuotesAndTrim(String value) {
        String trimmedValue = value.trim();
        if (trimmedValue.startsWith("\"") && trimmedValue.endsWith("\"")) {
            return trimmedValue.substring(1, trimmedValue.length() - 1).trim();
        }
        return trimmedValue;
    }

    /**
     * @param symbolTable
     * @param key
     * @param newValue
     */
    private void appendNewValueToExistingEntry(Map<String, String[]> symbolTable, final String key, final String newValue) {
        final String[] symbols = symbolTable.get(key);
        final String[] newSymbols = Arrays.copyOf(symbols, symbols.length + 1);
        newSymbols[symbols.length] = newValue;
        symbolTable.put(key, newSymbols);
    }

    /**
     * Look into a symbolTable, seeking the value for a particular variable. Basically, looking for the last concrete
     * assignment is the answer. If the last entry references another variable, drop this entry, and re-read the table,
     * seeking instead that other variable. This will recursively track down the value, short of going into other files.
     * 
     * @param propertyMap
     * @param variable
     * @return the value of the for the #variable with wrapping quotes removed, or null if the variable doesn't exist
     */
    protected void expandValue(Map<String, String[]> propertyMap, String variable, Set<String> expandedValues) {
        // If the variable doesn't exist ignore it, otherwise process it.
        if (propertyMap.containsKey(variable)) {
            String[] valuesForOurVariable = propertyMap.get(variable);
            for (String value : valuesForOurVariable) {
                if (!value.startsWith("$")) {
                    log.debug("About to unwrap last symbol '" + value + "'");
                    expandedValues.add(stripOutsideQuotesAndTrim(value));
                    continue;
                }

                // Take care of the hardest cases: the value references another variable. This means we need to
                // drop this entry from the symbol table and seek the referenced variable recursively in the property
                // map.

                // Extract the name of the referenced variable
                String referencedVariable = value.replaceAll("[${}]", ""); // Trim the '${}' from the string

                // Clone the propertyMap, dropping the entry for our referenced variable. Should eventually reduce to a
                // simpler situation covered earlier in
                // this algorithm.
                expandValue(copyTableAndDrop(propertyMap, variable, value), referencedVariable, expandedValues);
            }
        }
    }

    /**
     * Clone the existing symbol table, but drop the last item in the variable's list This will prevent the case of a
     * never-ending loop causing a Stackoverflow exception.
     * 
     * @param oldSymbolTable - the symbol table that needs to be cloned
     * @param variable - the variable that is being sought
     * @param variableToRemove Variable to remove from list of values.
     * @return a cloned table where the #variable's list of values has been trimmed by one item
     */
    private Map<String, String[]> copyTableAndDrop(Map<String, String[]> oldSymbolTable, String variable, String variableToRemove) {
        Map<String, String[]> newSymbolTable = new HashMap<String, String[]>();
        for (Entry<String, String[]> entry : oldSymbolTable.entrySet()) {
            if (entry.getKey().equals(variable)) {
                newSymbolTable.put(entry.getKey(), removeItemFromArray(entry.getValue(), variableToRemove));
            } else {
                newSymbolTable.put(entry.getKey(), entry.getValue());
            }
        }
        return newSymbolTable;
    }

    private String[] removeItemFromArray(String[] array, String valueToRemove) {
        List<String> newList = new ArrayList<String>();
        for (String value : array) {
            if (!valueToRemove.equals(value)) {
                newList.add(value);
            }
        }
        return newList.toArray(new String[0]);
    }
}
