package org.jboss.tools.cdi.seam.core.test.international;

import java.util.Set;

import org.jboss.tools.cdi.seam.core.international.BundleModelFactory;
import org.jboss.tools.cdi.seam.core.international.IBundle;
import org.jboss.tools.cdi.seam.core.international.IBundleModel;
import org.jboss.tools.cdi.seam.core.international.ILocalizedValue;
import org.jboss.tools.cdi.seam.core.international.IProperty;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.cdi.seam.solder.core.test.GenericBeanValidationTest;

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

	public void testIncrementalBuildAtAddRemoveExtension() throws Exception {
		String path = "WebContent/WEB-INF/lib/seam-international.jar";
		String original = "WebContent/WEB-INF/lib/seam-international.original";
		
		GenericBeanValidationTest.removeFile(getTestProject(), path);
		
		IBundleModel bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNull(bundleModel);

		GenericBeanValidationTest.writeFile(getTestProject(), original, path);
		bundleModel = BundleModelFactory.getBundleModel(getTestProject());
		assertNotNull(bundleModel);
		Set<String> bundles = bundleModel.getAllAvailableBundles();
		assertTrue(bundles.contains("com.sun.corba.se.impl.logging.LogStrings"));
	}
}