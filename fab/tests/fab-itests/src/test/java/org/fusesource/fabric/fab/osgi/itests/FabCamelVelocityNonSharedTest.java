package org.fusesource.fabric.fab.osgi.itests;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public class FabCamelVelocityNonSharedTest extends FabIntegrationTestSupport {

    @Override
    protected void doInstallFabricBundles() throws Exception {
        commandSession.execute("osgi:install fab:mvn:org.fusesource.fabric.fab.tests/fab-sample-camel-velocity-noshare");

        Thread.sleep(1000);

        assertStartBundle("org.fusesource.fabric.fab.tests.fab-sample-camel-velocity-noshare");
    }




}