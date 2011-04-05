package org.jboss.tools.cdi.core.test.extension;

import java.util.Set;

import org.jboss.tools.cdi.core.extension.CDIExtensionFactory;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;

import junit.framework.TestCase;

public class ExtensionFactoryTest extends TestCase {

	public void testExtensionFactory() throws Exception {
		Set<String> clss = CDIExtensionFactory.getInstance().getExtensionClassesByFeature(IProcessAnnotatedTypeFeature.class);
		System.out.println(clss.size());
	}

}
