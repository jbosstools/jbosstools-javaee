package org.jboss.tools.seam.core.test.project.facet;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.seam.internal.core.project.facet.ZipUtils;

import junit.framework.TestCase;

public class ZupUtilsTest extends TestCase {

	public void testGetZipFiles() throws IOException {
		File jar = FileLocator.getBundleFile(Platform.getBundle("org.eclipse.core.runtime"));
		String destinationName = System.getProperty("java.io.tmpdir");
		ZipUtils.getZipFiles(jar.getAbsolutePath(), destinationName, "");
	}

}
