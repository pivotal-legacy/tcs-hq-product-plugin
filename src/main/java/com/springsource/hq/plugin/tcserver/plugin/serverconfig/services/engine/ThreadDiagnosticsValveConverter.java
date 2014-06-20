// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.plugin.serverconfig.services.engine;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.springsource.hq.plugin.tcserver.plugin.serverconfig.AbstractXmlElementConverter;
import com.springsource.hq.plugin.tcserver.plugin.serverconfig.XmlElementConverter;
import com.springsource.hq.plugin.tcserver.serverconfig.services.engine.ThreadDiagnostics;

public class ThreadDiagnosticsValveConverter extends AbstractXmlElementConverter implements XmlElementConverter<ThreadDiagnostics> {

    private static final String VALVE = "Thread Diagnostics Valve";

    private static final String HISTORY = "history";

    private static final String THRESHOLD = "threshold";

    public ThreadDiagnostics convert(Element valve, Properties catalinaProperties) {
        final ThreadDiagnostics threadDiagnostics = new ThreadDiagnostics();
        String history = parseProperties(valve.getAttribute(HISTORY), catalinaProperties);
        if (!(EMPTY_STRING.equals(history))) {
            try {
                threadDiagnostics.setHistory(Long.valueOf(history));
            } catch (NumberFormatException e) {
                logNonNumericValue(VALVE, HISTORY, history);
            }
        }
        String threshold = parseProperties(valve.getAttribute(THRESHOLD), catalinaProperties);
        if (!(EMPTY_STRING.equals(threshold))) {
            try {
                threadDiagnostics.setThreshold(Long.valueOf(threshold));
            } catch (NumberFormatException e) {
                logNonNumericValue(VALVE, THRESHOLD, threshold);
            }
        }
        threadDiagnostics.setEnabled(true);
        return threadDiagnostics;
    }

    public void convert(Document document, Element threadDiagnosticsValve, ThreadDiagnostics from, Properties catalinaProperties) {
        setAttribute(threadDiagnosticsValve, HISTORY, from.getHistory(), catalinaProperties, false);
        setAttribute(threadDiagnosticsValve, THRESHOLD, from.getThreshold(), catalinaProperties, false);
    }
}
