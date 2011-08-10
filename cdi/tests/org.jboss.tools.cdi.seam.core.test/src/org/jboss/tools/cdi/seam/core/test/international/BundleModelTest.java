package org.jboss.tools.cdi.seam.core.test.international;

import java.util.Set;

import org.jboss.tools.cdi.seam.core.international.BundleModelFactory;
import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.cdi.seam.core.international.IBundleModel;
import org.jboss.tools.cdi.seam.core.international.ILocalizedValue;
import org.jboss.tools.cdi.seam.core.international.IProperty;

public class BundleModelTest extends SeamCoreTest {

	public void testBundleModel() throws Exception {
		IBundleModel bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(bundleModel);

		Set<String> bundles = bundleModel.getAllAvailableBundles();
		assertTrue(bundles.contains("messages"));

		IBundle bundle = bundleModel.getBundle("messages");
		assertNotNull(bundle);

		IProperty property = bundle.getProperty("home_header1");
		assertNotNull(property);

		ILocalizedValue value = property.getValue("de");
		assertNotNull(value);
		assertEquals("Über dieses Beispiel-Anwendung", value.getValue());
		
		value = property.getValue();
		assertNotNull(value);
		assertEquals("About this example application", value.getValue());
	}
}