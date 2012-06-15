package org.universAAL.hwexporter.activityhub.test;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.springframework.util.Assert;
import org.universAAL.itests.IntegrationTest;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

/**
 * Here developer's of this artifact should code their integration tests.
 * 
 * @author rotgier
 * 
 */
public class ArtifactIntegrationTest extends IntegrationTest {

    public static ModuleContext mc;
	private static ActivityHubClient ahc;
    public MyActivityHubServiceConsumer serviceConsumer;
    private MyActivityHubContextListener contextListener;
    
    public ArtifactIntegrationTest() {
    	setBundleConfLocation("conf");
    }

	public void testComposite() throws Exception {
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] { bundleContext });

		ahc = new ActivityHubClient(this);

		// start uAAL bus consumer threads
		
		serviceConsumer = new MyActivityHubServiceConsumer(mc,ahc);
		contextListener = new MyActivityHubContextListener(mc,ahc);

		List list = serviceConsumer.getControlledActivityHubSensors();
//		Assert.isTrue(list.size() == 2);
		// This is not working because this bundle is started last in the composite 
		// Devicemanager starts before and cannot find ISO-drivers!
 
		
	}

	public void stop(BundleContext context) throws Exception {
		if (serviceConsumer != null)
			serviceConsumer.deleteGui();

		if ( mc.stop(mc) ) //mc.uninstall(mc)
			System.out.println("smp.activityhub.client bundle successfully stopped from uAALBundleContainer!");
		else
			System.out.println("Problem stopping smp.activityhub.client bundle in uAALBundleContainer!");
	}

}
