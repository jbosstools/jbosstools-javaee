package org.jboss.tools.cdi.core.test;

import java.io.File;
import java.io.FileFilter;

import org.jboss.tools.common.util.FileUtil;

public class TCKUseCaseGenerator {
	static String TARGET_TCK_PATH = "C:/Works/Eclipse/3.5/workspace-M6a/org.jboss.tools.cdi.core.test/resources/tck";
	static String ORIGINAL_JAVA_SOURCE_PATH = "C:/Works/Eclipse/3.5/runtime-New_configuration/tck1/JavaSource";
	static String ORIGINAL_TEST_PATH = "C:/Works/Eclipse/3.5/runtime-New_configuration/tck/JavaSource/org/jboss/jsr299/tck/tests";
	static String PACKAGE = "org/jboss/jsr299/tck/tests";
	File targetTCK = new File(TARGET_TCK_PATH);
	File javaSource = new File(ORIGINAL_JAVA_SOURCE_PATH);
	File testPath = new File(ORIGINAL_TEST_PATH);
	

	public TCKUseCaseGenerator() {
		
	}

	public void process() {
		FileUtil.copyDir(testPath, targetTCK, true, true, true, new TestFileFilter());
	}

	class TestFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return pathname.isDirectory() || (!name.endsWith("Test.java"));
		}		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		TCKUseCaseGenerator g = new TCKUseCaseGenerator();
		g.process();
	}

}
