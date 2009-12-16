package org.jboss.tools.cdi.core.test.tck;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.osgi.framework.Bundle;

import junit.framework.TestCase;

public class TCKTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	protected static String PROJECT_PATH = "/projects/tck";
	protected static String JAVA_SOURCE = PROJECT_PATH + "/JavaSource";
	protected static String WEB_CONTENT = PROJECT_PATH + "WebContent";
	protected static String WEB_INF = WEB_CONTENT + "WEB-INF";

	protected static String JAVA_SOURCE_SUFFIX = "/JavaSource";
	protected static String WEB_CONTENT_SUFFIX = "/WebContent";
	protected static String WEB_INF_SUFFIX = "/WEB-INF";

	protected static String TCK_RESOURCES_PREFIX = "/resources/tck";

	public TCKTest() {}

	public IProject importPreparedProject(String _resourcePath) throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		String projectPath = FileLocator.resolve(b.getEntry(PROJECT_PATH)).getFile();
		String resourcePath = FileLocator.resolve(b.getEntry(_resourcePath)).getFile();
		
		File javaSourceFrom = new File(resourcePath + JAVA_SOURCE_SUFFIX);
		if(javaSourceFrom.isDirectory()) {
			File javaSourceTo = new File(projectPath + JAVA_SOURCE_SUFFIX);
			FileUtil.copyDir(javaSourceFrom, javaSourceTo);
		}
	
		File webContentFrom = new File(resourcePath + WEB_CONTENT_SUFFIX);
		if(webContentFrom.isDirectory()) {
			File webContentTo = new File(projectPath + WEB_CONTENT_SUFFIX);
			FileUtil.copyDir(webContentFrom, webContentTo);
		}
		File webInfFrom = new File(resourcePath + WEB_INF_SUFFIX);
		if(webInfFrom.isDirectory()) {
			File webInfTo = new File(projectPath + WEB_CONTENT_SUFFIX + WEB_INF_SUFFIX);
			FileUtil.copyDir(webInfFrom, webInfTo);
		}	
		
		return ResourcesUtils.importProject(b, PROJECT_PATH);
	}

	public void cleanProject(String _resourcePath) throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		String projectPath = FileLocator.resolve(b.getEntry(PROJECT_PATH)).getFile();
		String resourcePath = FileLocator.resolve(b.getEntry(_resourcePath)).getFile();
		
		File javaSourceFrom = new File(resourcePath + JAVA_SOURCE_SUFFIX);
		if(javaSourceFrom.isDirectory()) {
			File javaSourceTo = new File(projectPath + JAVA_SOURCE_SUFFIX);
			File[] fs = javaSourceTo.listFiles();
			if(fs != null) for (int i = 0; i < fs.length; i++) {
				if(fs[i].getName().equals("placeholder.txt")) continue;
				FileUtil.remove(fs[i]);
			}
		}
	
		File webContentFrom = new File(resourcePath + WEB_CONTENT_SUFFIX);
		if(webContentFrom.isDirectory()) {
			File webContentTo = new File(projectPath + WEB_CONTENT_SUFFIX);
			File[] fs = webContentTo.listFiles();
			if(fs != null) for (int i = 0; i < fs.length; i++) {
				if(fs[i].getName().equals("WEB-INF")) continue;
				if(fs[i].getName().equals("META-INF")) continue;
				FileUtil.remove(fs[i]);
			}
		}
		File webInfFrom = new File(resourcePath + WEB_INF_SUFFIX);
		if(webInfFrom.isDirectory()) {
			File webInfTo = new File(projectPath + WEB_CONTENT_SUFFIX + WEB_INF_SUFFIX);
			File[] fs = webInfTo.listFiles();
			if(fs != null) for (int i = 0; i < fs.length; i++) {
				if(fs[i].getName().equals("classes")) continue;
				if(fs[i].getName().equals("lib")) continue;
				FileUtil.remove(fs[i]);
			}
		}	
	}

	protected void setUp() throws Exception {
	}

	public void test1() {
		
	}

	protected void tearDown() throws Exception {
	}
}
