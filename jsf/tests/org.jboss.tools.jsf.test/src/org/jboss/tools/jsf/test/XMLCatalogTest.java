package org.jboss.tools.jsf.test;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;

public class XMLCatalogTest extends TestCase {

	public void testRichFacesSchemas() throws MalformedURLException, IOException {
		ICatalog catalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
		String uri = catalog.resolveURI("http://jboss.org/schema/richfaces/cdk/core");
		assertNotNull(uri);
		uri = catalog.resolveURI("http://jboss.org/schema/richfaces/cdk/ext");
		assertNotNull(uri);
		uri = catalog.resolveURI("http://jboss.org/schema/richfaces/cdk/jsf/composite");
		assertNotNull(uri);
		uri = catalog.resolveURI("http://jboss.org/schema/richfaces/cdk/jstl/core");
		assertNotNull(uri);
		uri = catalog.resolveURI("http://jboss.org/schema/richfaces/cdk/xhtml-el");
		assertNotNull(uri);
	}
}
