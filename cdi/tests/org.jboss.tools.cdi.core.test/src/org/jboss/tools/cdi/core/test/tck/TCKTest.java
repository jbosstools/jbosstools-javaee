package org.jboss.tools.cdi.core.test.tck;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
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

	protected IParametedType getType(String name) throws JavaModelException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), name);
		return type == null ? null : cdiProject.getNature().getTypeFactory().newParametedType(type);
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

	protected Set<IBean> getBeans(String typeName, String... qualifierNames) throws JavaModelException {
		IParametedType type = getType(typeName);
		assertNotNull("Can't find " + typeName + " type.", type);
		Set<IType> qualifiers = new HashSet<IType>();
		for (String name : qualifierNames) {
			IType qualifier = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), name);
			assertNotNull("Can't find " + name + " type.", qualifier);
			qualifiers.add(qualifier);
		}
		Set<IBean> beans = cdiProject.getBeans(true, type, qualifiers.toArray(new IType[0]));
		assertNotNull("There is no beans with " + typeName + " type", beans);
		return beans;
	}

	protected IClassBean getClassBean(String path) {
		IFile file = tckProject.getFile(path);
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		for (IBean bean : beans) {
			if(bean instanceof IClassBean) {
				return (IClassBean)bean;
			}
		}
		fail("Can't find a class bean in " + path);
		return null;
	}

	protected IDecorator getDecorator(String path) {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/TimestampLogger.java");
		assertNotNull("Can't find the bean.", bean);
		if(!(bean instanceof IDecorator)) {
			fail("The bean is not a decorator.");
		}
		return (IDecorator)bean;
	}

	protected IQualifierDeclaration getQualifierDeclarationFromClass(String beanFilePath, String annotationTypeName) throws JavaModelException {
		IFile file = tckProject.getFile(beanFilePath);
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertFalse("Can't find any bean in " + beanFilePath, beans.isEmpty());
		for (IBean bean : beans) {
			Set<IQualifierDeclaration> declarations = bean.getQualifierDeclarations();
			IParametedType type = getType(annotationTypeName);
			for (IQualifierDeclaration declaration : declarations) {
				IAnnotation annotation = declaration.getDeclaration();
				if(type.getType().getElementName().equals(annotation.getElementName())) {
					return declaration;
				}
			}
		}
		fail("Can't find " + annotationTypeName + " qualifier in " + beanFilePath);
		return null;
	}

	protected IAnnotationDeclaration createAnnotationDeclarationForAnnotation(String beanClassFilePath, String annotationTypeName) throws JavaModelException {
		IFile file = tckProject.getFile(beanClassFilePath);
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		IBean bean = beans.iterator().next();
		final IType beanClass = bean.getBeanClass();
		final IParametedType type = getType(annotationTypeName);
		final IAnnotation annotation = beanClass.getAnnotation(type.getType().getElementName());
		IAnnotationDeclaration annotationDeclaration = new IAnnotationDeclaration() {
			public IAnnotation getDeclaration() {
				return annotation;
			}

			public IMember getParentMember() {
				return beanClass;
			}

			public IType getType() {
				return type.getType();
			}

			public int getLength() {
				return 0;
			}

			public int getStartPosition() {
				return 0;
			}

			public ICDIAnnotation getAnnotation() {
				return null;
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
			return (pathname.isDirectory() && !name.endsWith(".svn")) || name.endsWith(".jsp") || name.endsWith(".xhtml");
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

	protected void assertContainsBeanTypes(IBean bean, String... typeNames) {
		assertContainsBeanTypes(true, bean, typeNames);
	}

	protected void assertContainsBeanTypes(boolean checkTheNumberOfTypes, IBean bean, String... typeNames) {
		if(checkTheNumberOfTypes) {
			assertEquals("Wrong number of types.", typeNames.length, bean.getLegalTypes().size());
		}
		for (String typeName : typeNames) {
			Set<IParametedType> types = bean.getLegalTypes();
			StringBuffer allTypes = new StringBuffer("[");
			boolean found = false;
			for (IParametedType type : types) {
				allTypes.append(" ").append(type.getType().getFullyQualifiedName()).append(";");
				if (typeName.equals(type.getType().getFullyQualifiedName())) {
					found = true;
					break;
				}
			}
			allTypes.append("]");
			assertTrue(bean.getResource().getFullPath() + " bean " + allTypes.toString() + " should have " + typeName + " type.", found);
		}
	}

	protected void assertContainsBeanTypeSignatures(IBean bean, String... typeSignatures) {
		assertContainsBeanTypeSignatures(true, bean, typeSignatures);
	}

	protected void assertContainsBeanTypeSignatures(boolean checkTheNumberOfTypes, IBean bean, String... typeSignatures) {
		if(checkTheNumberOfTypes) {
			assertEquals("Wrong number of types.", typeSignatures.length, bean.getLegalTypes().size());
		}
		for (String typeSignature : typeSignatures) {
			Set<IParametedType> types = bean.getLegalTypes();
			StringBuffer allTypes = new StringBuffer("[");
			boolean found = false;
			for (IParametedType type : types) {
				allTypes.append(" ").append(type.getSignature()).append(";");
				if (typeSignature.equals(type.getSignature())) {
					found = true;
					break;
				}
			}
			allTypes.append("]");
			assertTrue(bean.getResource().getFullPath() + " bean " + allTypes.toString() + " should have " + typeSignature + " type signature.", found);
		}
	}

	public static void assertDoesNotContainBeanClasses(Set<IBean> beans, String... beanClassNames) throws CoreException {
		StringBuffer sb = new StringBuffer("[");
		for (String beanClassName : beanClassNames) {
			sb.append(beanClassName).append("; ");
		}
		sb.append("]");
		for (String beanClassName : beanClassNames) {
			assertTrue("Found " + beanClassName + " among " + sb.toString(), doesNotContainBeanClass(beans, beanClassName));
		}
	}

	protected IInjectionPointField getInjectionPointField(String beanClassFilePath, String fieldName) {
		IFile file = tckProject.getFile(beanClassFilePath);
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertEquals("Wrong number of the beans", 1, beans.size());
		Set<IInjectionPoint> injections = beans.iterator().next().getInjectionPoints();
		for (IInjectionPoint injectionPoint : injections) {
			if(injectionPoint instanceof IInjectionPointField) {
				IInjectionPointField field = (IInjectionPointField)injectionPoint;
				if(fieldName.equals(field.getField().getElementName())) {
					return field;
				}
			}
		}
		fail("Can't find \"" + fieldName + "\" injection point filed in " + beanClassFilePath);
		return null;
	}

	public static void assertContainsBeanClasses(Set<IBean> beans, String... beanClassNames) throws CoreException {
		assertContainsBeanClasses(true, beans, beanClassNames);
	}

	public static void assertContainsBeanClasses(boolean checkTheNumberOfBeans, Set<IBean> beans, String... beanClassNames) throws CoreException {
		if(checkTheNumberOfBeans) {
			assertEquals("Wrong number of beans.", beanClassNames.length, beans.size());
		}
		StringBuffer sb = new StringBuffer("[");
		for (String beanClassName : beanClassNames) {
			sb.append(beanClassName).append("; ");
		}
		sb.append("]");
		for (String beanClassName : beanClassNames) {
			assertTrue("Didn't found " + beanClassName + " among " + sb.toString(), containsBeanClass(beans, beanClassName));
		}
	}

	public static void assertContainsBeanClass(Set<IBean> beans, String beanClassName) throws CoreException {
		assertTrue("Didn't find " + beanClassName, containsBeanClass(beans, beanClassName));
	}

	private static boolean doesNotContainBeanClass(Set<IBean> beans, String beanClassName) throws CoreException {
		for (IBean bean : beans) {
			if(beanClassName.equals(bean.getBeanClass().getFullyQualifiedName())) {
				return false;
			}
		}
		return true;
	}

	private static boolean containsBeanClass(Set<IBean> beans, String beanClassName) throws CoreException {
		for (IBean bean : beans) {
			if(beanClassName.equals(bean.getBeanClass().getFullyQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	public static void assertContainsTypes(Set<IParametedType> types, String... typeNames) throws CoreException {
		assertContainsTypes(true, types, typeNames);
	}

	public static void assertContainsTypes(boolean checkTheNumberOfTypes, Set<IParametedType> types, String... typeNames) throws CoreException {
		if(checkTheNumberOfTypes) {
			assertEquals("The number of types should be the same", typeNames.length, types.size());
		}
		for (String typeName : typeNames) {
			assertContainsType(types, typeName);
		}
	}

	public static void assertContainsType(Set<IParametedType> types, String typeName) throws CoreException {
		StringBuffer allTheTypes = new StringBuffer("[ ");
		for (IParametedType type : types) {
			allTheTypes.append(type.getType().getFullyQualifiedName()).append(" ,");
		}
		allTheTypes.append(" ]");

		for (IParametedType type : types) {
			assertTrue("The set of types " + allTheTypes.toString() + " doesn't contain " + type.getType().getFullyQualifiedName() + " type.", containsType(types, typeName));
		}
	}

	private static boolean containsType(Set<IParametedType> types, String typeName) throws CoreException {
		for (IParametedType type : types) {
			if(typeName.equals(type.getType().getFullyQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	public static void assertContainsQualifier(IBean bean, IQualifierDeclaration declaration) throws CoreException {
		String typeName = declaration.getQualifier().getSourceType().getFullyQualifiedName();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		StringBuffer allTypes = new StringBuffer("[");
		boolean found = false;
		for (IQualifier qualifier : qualifiers) {
			allTypes.append(" ").append(qualifier.getSourceType().getFullyQualifiedName()).append(";");
			if (typeName.equals(qualifier.getSourceType().getFullyQualifiedName())) {
				found = true;
				break;
			}
		}
		allTypes.append("]");
		assertTrue(bean.getResource().getFullPath() + " bean (qualifiers - " + allTypes.toString() + ") should have the qualifier with " + typeName + " type.", found);
		Set<IQualifierDeclaration> declarations = bean.getQualifierDeclarations(true);
		for (IQualifierDeclaration d : declarations) {
			if(CDIProject.getAnnotationDeclarationKey(d).equals(CDIProject.getAnnotationDeclarationKey(declaration)) ) {
				return;
			}
		}
		fail(bean.getResource().getFullPath() + " bean (qualifiers - " + allTypes.toString() + ") should have the qualifier declaration with " + typeName + " type.");
	}

	public static void assertContainsQualifierType(IBean bean, String... typeNames) {
		assertContainsQualifierType(false, bean, typeNames);
	}

	public static void assertContainsQualifierType(boolean theNumbersOfQualifierShouldBeTheSame, IBean bean, String... typeNames) {
		Set<IQualifier> qualifiers = bean.getQualifiers();

		if(theNumbersOfQualifierShouldBeTheSame) {
			assertEquals("Defferent numbers of qualifiers", typeNames.length, qualifiers.size());
		}

		StringBuffer allTypes = new StringBuffer("[");
		for (IQualifier qualifier : qualifiers) {
			allTypes.append(" ").append(qualifier.getSourceType().getFullyQualifiedName()).append(";");
		}
		allTypes.append("]");

		for (String typeName : typeNames) {
			boolean found = false;
			for (IQualifier qualifier : qualifiers) {
				if (typeName.equals(qualifier.getSourceType().getFullyQualifiedName())) {
					found = true;
					break;
				}
			}
			assertTrue(bean.getResource().getFullPath() + " bean (qualifiers - " + allTypes.toString() + ") should have the qualifier with " + typeName + " type.", found);
		}
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