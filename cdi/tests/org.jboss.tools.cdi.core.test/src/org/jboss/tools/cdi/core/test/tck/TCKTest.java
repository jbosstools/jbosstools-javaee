/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.impl.JavaAnnotation;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.osgi.framework.Bundle;

public class TCKTest extends TestCase {
	protected final static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";

	public final static String JAVA_SOURCE_SUFFIX = "/JavaSource";
	public final static String WEB_CONTENT_SUFFIX = "/WebContent";
	public final static String WEB_INF_SUFFIX = "/WEB-INF";

	private ITCKProjectNameProvider projectNameProvider;

	protected IProject tckProject;
	protected IProject rootProject;
	protected IProject parentProject;
	protected ICDIProject cdiProject;

	public TCKTest() {
		this.projectNameProvider = getProjectNameProvider();
		tckProject = getTestProject();
		parentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectNameProvider.getProjectNames()[0]);
		rootProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectNameProvider.getProjectNames()[2]);
		cdiProject = CDICorePlugin.getCDIProject(tckProject, false);
	}

	protected void setUp() throws Exception {
		cdiProject = CDICorePlugin.getCDIProject(tckProject, false);
	}

	protected int getVersionIndex() {
		return cdiProject.getVersion() == null ? 0 : cdiProject.getVersion().getIndex();
	}

	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK10ProjectNameProvider();
	}

	protected void deleteTestProject() throws Exception {
		rootProject.delete(true, true, null);
		tckProject.delete(true, true, null);
		parentProject.delete(true, true, null);
	}

	public IProject getTestProject() {
		if(tckProject==null) {
			try {
				tckProject = findTestProject();
				if(!tckProject.exists()) {
//					ValidatorManager.setStatus(CoreValidationTest.VALIDATION_STATUS);
					tckProject = importPreparedProject();
//					TestUtil._waitForValidation(tckProject);
//					TestUtil.waitForValidation();
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

	public IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectNameProvider.getMainProjectName());
	}

	public IProject[] importPreparedProjects() throws Exception {
		List<IProject> projects = new ArrayList<IProject>();
		importPreparedProject();
		for (String name : projectNameProvider.getProjectNames()) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			assertTrue(project.exists());
			projects.add(project);
		}
		return projects.toArray(new IProject[projects.size()]);		
	}

	public IProject importPreparedProject() throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		IProject tckP = ResourcesPlugin.getWorkspace().getRoot().getProject(projectNameProvider.getMainProjectName());
		if(!tckP.exists()) {
			for (String name : projectNameProvider.getProjectNames()) {
				IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
				assertFalse("Error during importing TCK Project. Project " + p.getName() + " already exists.", p.exists());
			}
			for (String path : projectNameProvider.getProjectPaths()) {
				IProject project = ResourcesUtils.importProject(b, path);
				project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
			}
		}
		tckP.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		TestUtil._waitForValidation(tckP);
		return tckP;
	}

	protected Collection<IBean> getBeans(String typeName, String... qualifierNames) throws JavaModelException {
		return getBeans(true, typeName, qualifierNames);
	}

	protected Collection<IBean> getBeans(boolean resolve, String typeName, String... qualifierNames) throws JavaModelException {
		IParametedType type = getType(typeName);
		assertNotNull("Can't find " + typeName + " type.", type);
		Collection<IType> qualifiers = new HashSet<IType>();
		for (String name : qualifierNames) {
			IType qualifier = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), name);
			assertNotNull("Can't find " + name + " type.", qualifier);
			qualifiers.add(qualifier);
		}
		Collection<IBean> beans = cdiProject.getBeans(resolve, type, qualifiers.toArray(new IType[0]));
		assertNotNull("There is no beans with " + typeName + " type", beans);
		return beans;
	}

	protected IClassBean getClassBean(String path) {
		return getClassBean(null, path);
	}

	protected IClassBean getClassBean(String fullyQualifiedTypeName, String path) {
		IFile file = tckProject.getFile(path);
		assertTrue(file.exists());
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		for (IBean bean : beans) {
			if(bean instanceof IClassBean && (fullyQualifiedTypeName==null || fullyQualifiedTypeName.equals(bean.getBeanClass().getFullyQualifiedName()))) {
				return (IClassBean)bean;
			}
		}
		fail("Can't find a class bean in " + path);
		return null;
	}

	protected IDecorator getDecorator(String path) {
		IClassBean bean = getClassBean(path);
		assertNotNull("Can't find the bean.", bean);
		if(!(bean instanceof IDecorator)) {
			fail("The bean is not a decorator.");
		}
		return (IDecorator)bean;
	}

	protected IQualifierDeclaration getQualifierDeclarationFromClass(String beanFilePath, String annotationTypeName) throws JavaModelException {
		IFile file = tckProject.getFile(beanFilePath);
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		assertFalse("Can't find any bean in " + beanFilePath, beans.isEmpty());
		for (IBean bean : beans) {
			Collection<IQualifierDeclaration> declarations = bean.getQualifierDeclarations();
			IParametedType type = getType(annotationTypeName);
			for (IQualifierDeclaration declaration : declarations) {
				if(declaration.getType() != null && type.getType().getElementName().equals(declaration.getType().getElementName())) {
					return declaration;
				}
			}
		}
		fail("Can't find " + annotationTypeName + " qualifier in " + beanFilePath);
		return null;
	}

	protected IAnnotationDeclaration createAnnotationDeclarationForAnnotation(String beanClassFilePath, String annotationTypeName) throws JavaModelException {
		IFile file = tckProject.getFile(beanClassFilePath);
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		IBean bean = beans.iterator().next();
		IType beanClass = bean.getBeanClass();
		final IParametedType type = getType(annotationTypeName);
		IAnnotation annotation = beanClass.getAnnotation(type.getType().getElementName());
		AnnotationDeclaration annotationDeclaration = new AnnotationDeclaration() {
			public IType getType() {
				return type.getType();
			}
			public int getLength() {
				return 0;
			}
			public int getStartPosition() {
				return 0;
			}
		};
		annotationDeclaration.setDeclaration(new JavaAnnotation(annotation, beanClass));
		return annotationDeclaration;
	}

	protected void assertTheOnlyBean(String typeName) throws JavaModelException {
		Collection<IBean> beans = getBeans(typeName);
		assertEquals("There should be the only bean with " + typeName + " type", 1, beans.size());
	}

	public static void assertLocationEquals(IFile file, Collection<? extends ITextSourceReference> references, String wrappingText, int startPosition, int length) throws CoreException {
		assertNotNull(wrappingText);
		int correctedStart = startPosition;
		String c = FileUtil.readStream(file);
		int i = c.indexOf(wrappingText);
		if(i >= 0) correctedStart += i;
		for (ITextSourceReference reference : references) {
			if(reference.getStartPosition()==correctedStart) {
				assertLocationEquals(file, wrappingText, reference, startPosition, length);
				return;
			}
		}
		StringBuffer message = new StringBuffer("Location [start position=").append(startPosition).append(", lengt=").append(length).append("] has not been found among the following locations: (");
		for (ITextSourceReference reference : references) {
			message.append("[start position=").append(reference.getStartPosition()).append(", length=").append(reference.getLength()).append("], ");
		}
		message.append(")");
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
			Collection<IParametedType> types = bean.getLegalTypes();
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
			Collection<IParametedType> types = bean.getLegalTypes();
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

	public static void assertDoesNotContainBeanClasses(Collection<IBean> beans, String... beanClassNames) throws CoreException {
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
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		Iterator<IBean> it = beans.iterator();
		while(it.hasNext()) {
			IBean b = it.next();
			if(b instanceof IProducer) it.remove();
		}
		assertEquals("Wrong number of the beans", 1, beans.size());
		Collection<IInjectionPoint> injections = beans.iterator().next().getInjectionPoints();
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

	protected IInjectionPointParameter getInjectionPointParameter(String beanClassFilePath, String methodName) {
		IFile file = tckProject.getFile(beanClassFilePath);
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		Iterator<IBean> it = beans.iterator();
		while(it.hasNext()) {
			IBean b = it.next();
			if(b instanceof IProducer) it.remove();
		}
		assertEquals("Wrong number of the beans", 1, beans.size());
		Collection<IInjectionPoint> injections = beans.iterator().next().getInjectionPoints();
		for (IInjectionPoint injectionPoint : injections) {
			if(injectionPoint instanceof IInjectionPointParameter) {
				IInjectionPointParameter param = (IInjectionPointParameter)injectionPoint;
				if(methodName.equals(param.getBeanMethod().getMethod().getElementName())) {
					return param;
				}
			}
		}
		fail("Can't find injection point parameter in method \"" + methodName + "\" in " + beanClassFilePath);
		return null;
	}

	public static void assertContainsBeanClasses(Collection<IBean> beans, String... beanClassNames) throws CoreException {
		assertContainsBeanClasses(true, beans, beanClassNames);
	}

	public static void assertContainsBeanClasses(boolean checkTheNumberOfBeans, Collection<IBean> beans, String... beanClassNames) throws CoreException {
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

	public static void assertContainsBeanClass(Collection<IBean> beans, String beanClassName) throws CoreException {
		assertTrue("Didn't find " + beanClassName, containsBeanClass(beans, beanClassName));
	}

	private static boolean doesNotContainBeanClass(Collection<IBean> beans, String beanClassName) throws CoreException {
		for (IBean bean : beans) {
			if(beanClassName.equals(bean.getBeanClass().getFullyQualifiedName())) {
				return false;
			}
		}
		return true;
	}

	private static boolean containsBeanClass(Collection<IBean> beans, String beanClassName) throws CoreException {
		for (IBean bean : beans) {
			if(beanClassName.equals(bean.getBeanClass().getFullyQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	public static void assertContainsTypes(Collection<IParametedType> types, String... typeNames) throws CoreException {
		assertContainsTypes(true, types, typeNames);
	}

	public static void assertContainsTypes(boolean checkTheNumberOfTypes, Collection<IParametedType> types, String... typeNames) throws CoreException {
		if(checkTheNumberOfTypes) {
			assertEquals("The number of types should be the same", typeNames.length, types.size());
		}
		for (String typeName : typeNames) {
			assertContainsType(types, typeName);
		}
	}

	public static void assertContainsType(Collection<IParametedType> types, String typeName) throws CoreException {
		StringBuffer allTheTypes = new StringBuffer("[ ");
		for (IParametedType type : types) {
			allTheTypes.append(type.getType().getFullyQualifiedName()).append(" ,");
		}
		allTheTypes.append(" ]");

		for (IParametedType type : types) {
			assertTrue("The set of types " + allTheTypes.toString() + " doesn't contain " + type.getType().getFullyQualifiedName() + " type.", containsType(types, typeName));
		}
	}

	private static boolean containsType(Collection<IParametedType> types, String typeName) throws CoreException {
		for (IParametedType type : types) {
			if(typeName.equals(type.getType().getFullyQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	public static void assertContainsQualifier(IBean bean, IQualifierDeclaration declaration) throws CoreException {
		String typeName = declaration.getQualifier().getSourceType().getFullyQualifiedName();
		Collection<IQualifier> qualifiers = bean.getQualifiers();
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
		Collection<IQualifierDeclaration> declarations = bean.getQualifierDeclarations(true);
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
		Collection<IQualifier> qualifiers = bean.getQualifiers();

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

	public static void assertLocationEquals(IFile file, String wrappingText, ITextSourceReference reference, int startPosition, int length) throws CoreException {
		assertNotNull(wrappingText);
		String c = FileUtil.readStream(file);
		int i = c.indexOf(wrappingText);
		if(i >= 0) startPosition += i;
		assertEquals("Wrong start position", startPosition, reference.getStartPosition());
		assertEquals("Wrong length", length, reference.getLength());
	}
}