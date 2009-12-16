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
		File[] fs = testPath.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isDirectory()) {
				processGroupOfTests(new File(targetTCK, fs[i].getName()), fs[i], PACKAGE + "/" + fs[i].getName());
			}
		}
	}

	public void processGroupOfTests(File target, File source, String packagePath) {
		target.mkdirs();
		File[] fs = source.listFiles();
		boolean hasTest = false;
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isDirectory()) {
				String n = fs[i].getName();
				if(n.length() > 0) n = n.substring(0, 1).toUpperCase() + n.substring(1);
				processFolder(target, fs[i], n, packagePath + "/" + fs[i].getName());
			} else if(fs[i].isFile()) {
				hasTest = true;
			}
		}
		if(hasTest) {
			String n = source.getName();
			n = n.substring(0, 1).toUpperCase() + n.substring(1);
			processTest(target, source, n, packagePath);
		}
		
	}

	void processFolder(File target, File source, String targetName, String packagePath) {
		File[] fs = source.listFiles();
		boolean hasTest = false;
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isDirectory()) {
				String n = fs[i].getName();
				if(n.length() > 0) n = n.substring(0, 1).toUpperCase() + n.substring(1);
				processFolder(target, fs[i], targetName + n, packagePath + "/" + fs[i].getName());
			} else if(fs[i].isFile()) {
				hasTest = true;
			}
		}
		if(hasTest) {
			processTest(target, source, targetName, packagePath);
		}
	}

	void processTest(File target, File source, String targetName, String packagePath) {
		target = new File(target, targetName);
		target.mkdirs();
	
		File targetJavaSource = new File(target, "JavaSource");
		targetJavaSource.mkdirs();
		FileUtil.copyDir(javaSource, true, targetJavaSource);
		
		File targetPackage = new File(targetJavaSource, packagePath);
		targetPackage.mkdirs();
		FileUtil.copyDir(source, targetPackage, false, true, true, new JavaFileFilter());

		File targetWebinf = new File(target, "WEB-INF");
		targetWebinf.mkdirs();
		FileUtil.copyDir(source, targetWebinf, false, true, true, new XmlFileFilter());		
		if(targetWebinf.listFiles().length == 0) {
			targetWebinf.delete();
		}
		
		File targetWebContent = new File(target, "WebContent");
		targetWebContent.mkdirs();
		FileUtil.copyDir(source, targetWebContent, false, true, true, new PageFileFilter());		
		if(targetWebContent.listFiles().length == 0) {
			targetWebContent.delete();
		}
	}

	class JavaFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return pathname.isDirectory() || name.endsWith(".java");
		}		
	}
	class XmlFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return pathname.isDirectory() || name.endsWith(".xml");
		}		
	}
	class PageFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return pathname.isDirectory() || (!name.endsWith(".xml") && !name.endsWith(".java"));
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
