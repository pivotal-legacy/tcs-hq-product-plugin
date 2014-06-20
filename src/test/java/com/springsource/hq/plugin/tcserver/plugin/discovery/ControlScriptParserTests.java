
package com.springsource.hq.plugin.tcserver.plugin.discovery;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.hyperic.hq.product.PluginException;
import org.junit.Test;

public class ControlScriptParserTests {

    private final ControlScriptParser controlScriptParser = new ControlScriptParser();

    private final File expectedInstallBase = new File("src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/");

    @Test
    public void tcServer21to26BashScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.1-to-2.6.sh"));

        assertEquals(this.expectedInstallBase, installBase);
    }

    @Test
    public void tcServer21to26BatScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.1-to-2.6.bat"));

        assertEquals(this.expectedInstallBase, installBase);
    }

    @Test
    public void tcServer20BashScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.0.sh"));

        assertEquals(this.expectedInstallBase, installBase);
    }

    @Test
    public void tcServer20BatScript() throws PluginException {
        File installBase = this.controlScriptParser.getInstallBase("catalina-base", new File(
            "src/test/resources/com/springsource/hq/plugin/tcserver/plugin/discovery/test-instance/bin/tcruntime-ctl-2.0.bat"));

        assertEquals(this.expectedInstallBase, installBase);
    }
}
