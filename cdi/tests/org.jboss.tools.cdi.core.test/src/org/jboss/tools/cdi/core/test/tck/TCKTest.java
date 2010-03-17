package org.jboss.tools.cdi.core.test.tck;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.osgi.framework.Bundle;

public class TCKTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	protected static String PROJECT_NAME = "tck";
	protected static String PROJECT_PATH = "/projects/tck";

	protected static String JAVA_SOURCE_SUFFIX = "/JavaSource";
	protected static String WEB_CONTENT_SUFFIX = "/WebContent";
	protected static String WEB_INF_SUFFIX = "/WEB-INF";
//	protected static String JAVA_SOURCE = PROJECT_PATH + JAVA_SOURCE_SUFFIX;
//	protected static String WEB_CONTENT = PROJECT_PATH + WEB_CONTENT_SUFFIX;
//	protected static String WEB_INF = WEB_CONTENT + WEB_INF_SUFFIX;

	static String PACKAGE = "/org/jboss/jsr299/tck";

	protected static String TCK_RESOURCES_PREFIX = "/resources/tck";

	protected IProject tckProject;
	protected ICDIProject cdiProject;

	public TCKTest() {
		tckProject = getTestProject();
		cdiProject = CDICorePlugin.getCDIProject(tckProject, false);
	}

	public IProject getTestProject() {
		if(tckProject==null) {
			try {
				tckProject = findTestProject();
				if(tckProject==null || !tckProject.exists()) {
					tckProject = importPreparedProject("/");
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return tckProject;
	}

	protected IType getType(String name) throws JavaModelException {
		return EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), name);
	}

	public static IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public static IProject importPreparedProject(String packPath) throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if(project==null || !project.exists()) {
			project = ResourcesUtils.importProject(b, PROJECT_PATH);
		}
		String projectPath = project.getLocation().toOSString();
		String resourcePath = FileLocator.resolve(b.getEntry(TCK_RESOURCES_PREFIX)).getFile();
		
		File from = new File(resourcePath + packPath);
		if(from.isDirectory()) {
			File javaSourceTo = new File(projectPath + JAVA_SOURCE_SUFFIX + PACKAGE + packPath);
			FileUtil.copyDir(from, javaSourceTo, true, true, true, new JavaFileFilter());

			File webContentTo = new File(projectPath + WEB_CONTENT_SUFFIX);
			FileUtil.copyDir(from, webContentTo, true, true, true, new PageFileFilter());

			File webInfTo = new File(projectPath + WEB_CONTENT_SUFFIX + WEB_INF_SUFFIX);
			FileUtil.copyDir(from, webInfTo, true, true, true, new XmlFileFilter());
		}
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
		return project;
	}

	protected Set<IBean> getBeans(String typeName) throws JavaModelException {
		IType type = getType(typeName);
		assertNotNull("Can't find " + typeName + " type.", type);
		Set<IBean> beans = cdiProject.getBeans(true, type, new IType[0]);
		assertNotNull("There is no eny beans with " + typeName + " type", beans);
		return beans;
	}

	protected IAnnotationDeclaration getAnnotationDeclarationFromBean(String beanClassFilePath, String annotationTypeName) throws JavaModelException {
		IFile file = tckProject.getFile(beanClassFilePath);
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		IBean bean = beans.iterator().next();
		final IType beanClass = bean.getBeanClass();
		final IType type = getType(annotationTypeName);
		final IAnnotation annotation = beanClass.getAnnotation(type.getElementName());
		IAnnotationDeclaration annotationDeclaration = new IAnnotationDeclaration() {
			public IAnnotation getDeclaration() {
				return annotation;
			}

			public IMember getParentMember() {
				return beanClass;
			}

			public IType getType() {
				return type;
			}

			public int getLength() {
				return 0;
			}

			public int getStartPosition() {
				return 0;
			}
		};
		return annotationDeclaration;
	}

	protected void assertTheOnlyBean(String typeName) throws JavaModelException {
		Set<IBean> beans = getBeans(typeName);
		assertEquals("There should be the only bean with " + typeName + " type", 1, beans.size());
	}

	static class JavaFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return (pathname.isDirectory() && !name.endsWith(".svn")) || (name.endsWith(".java") && !name.endsWith("Test.java"));
		}
	}

	static class XmlFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return (pathname.isDirectory() && !name.endsWith(".svn")) || name.endsWith(".xml");
		}		
	}

	static class PageFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			String name = pathname.getName();
			return (pathname.isDirectory() && !name.endsWith(".svn")) && !name.endsWith(".xml");
		}		
	}

	public static void assertLocationEquals(Set<? extends ITextSourceReference> references, int startPosition, int length) {
		for (ITextSourceReference reference : references) {
			if(reference.getStartPosition()==startPosition) {
				assertLocationEquals(reference, startPosition, length);
				return;
			}
		}
		StringBuffer message = new StringBuffer("Location [start positopn=").append(startPosition).append(", lengt=").append(length).append("] has not been found among ");
		for (ITextSourceReference reference : references) {
			message.append("[").append(reference.getStartPosition()).append(", ").append(reference.getLength()).append("] ");
		}
		fail(message.toString());
	}

	protected void assertContainsBeanType(IBean bean, String typeName) {
		Set<IParametedType> types = bean.getLegalTypes();
		StringBuffer allTypes = new StringBuffer("[");
		for (IParametedType type : types) {
			allTypes.append(" ").append(type.getType().getFullyQualifiedName()).append(";");
			if (typeName.equals(type.getType().getFullyQualifiedName())) {
				return;
			}
		}
		allTypes.append("]");
		fail(bean.getResource().getFullPath() + " bean " + allTypes.toString() + " should have " + typeName + " type.");
	}

	public static void assertContainsQualifierType(IBean bean, String typeName) {
		Set<IQualifier> qualifiers = bean.getQualifiers();
		StringBuffer allTypes = new StringBuffer("[");
		for (IQualifier qualifier : qualifiers) {
			allTypes.append(" ").append(qualifier.getSourceType().getFullyQualifiedName()).append(";");
			if (typeName.equals(qualifier.getSourceType().getFullyQualifiedName())) {
				return;
			}
		}
		allTypes.append("]");
		fail(bean.getResource().getFullPath() + " bean (qualifiers - " + allTypes.toString() + ") should have the qualifier with " + typeName + " type.");
	}

	public static void assertLocationEquals(ITextSourceReference reference, int startPosition, int length) {
		assertEquals("Wrong start position", startPosition, reference.getStartPosition());
		assertEquals("Wrong length", length, reference.getLength());
	}

	public static void cleanProject(String _resourcePath) throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		String projectPath = FileLocator.resolve(b.getEntry(PROJECT_PATH)).getFile();
		
		File javaSourceTo = new File(projectPath + JAVA_SOURCE_SUFFIX);
		File[] fs = javaSourceTo.listFiles();
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(fs[i].getName().equals(".svn")) continue;
			if(fs[i].getName().equals("placeholder.txt")) continue;
			FileUtil.remove(fs[i]);
		}

		File webContentTo = new File(projectPath + WEB_CONTENT_SUFFIX);
		fs = webContentTo.listFiles();
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(fs[i].getName().equals(".svn")) continue;
			if(fs[i].getName().equals("WEB-INF")) continue;
			if(fs[i].getName().equals("META-INF")) continue;
			FileUtil.remove(fs[i]);
		}

		File webInfTo = new File(projectPath + WEB_CONTENT_SUFFIX + WEB_INF_SUFFIX);
		fs = webInfTo.listFiles();
		if(fs != null) for (int i = 0; i < fs.length; i++) {
			if(fs[i].getName().equals(".svn")) continue;
			if(fs[i].getName().equals("classes")) continue;
			if(fs[i].getName().equals("lib")) continue;
			FileUtil.remove(fs[i]);
		}
	}
}