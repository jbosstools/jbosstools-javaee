package org.jboss.tools.cdi.core.test.extension;

import java.util.Set;

import org.jboss.tools.cdi.core.extension.CDIExtensionFactory;

import junit.framework.TestCase;

public class ExtensionFactoryTest extends TestCase {

	public void testExtensionFactory() throws Exception {
		Set<String> clss = CDIExtensionFactory.getInstance().getExtensionClassesByfeature("feature_1");
		System.out.println(clss.size());
	}

}
