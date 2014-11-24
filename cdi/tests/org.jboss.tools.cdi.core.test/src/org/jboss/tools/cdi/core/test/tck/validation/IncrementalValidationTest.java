/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.FilterUtil;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.operations.ValidatorSubsetOperation;
import org.jboss.tools.cdi.core.CDICoreBuilder;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.util.FileUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class IncrementalValidationTest extends ValidationTest {

	/**
	 * See https://issues.jboss.org/browse/JBIDE-8325
	 * @throws Exception
	 */
	public void testInjectionPointRevalidation() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile testInjection = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanBroken.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);
	
			IFile testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.java");
			IFile testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.validation");
			testBean.setContents(testBeanImpl.getContents(), IFile.FORCE, new NullProgressMonitor());
	
			TestUtil.validate(testBean);
	
			AbstractResourceMarkerTest.assertMarkerIsCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
	
			testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.java");
			testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2Original.validation");
			testBeanImpl.setContents(testBean.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(testBeanImpl);
	
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9071
	 * @throws Exception
	 */
	public void testInjectionPointResolvedToProducerRevalidation() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile testInjection = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanForProducerBroken.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);
	
			IFile testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.java");
			IFile testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.validation");
	
			testBean.setContents(testBeanImpl.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(testBean);
	
			AbstractResourceMarkerTest.assertMarkerIsCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
	
			testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.java");
			testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlaceOriginal.validation");
	
			testBeanImpl.setContents(testBean.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(testBeanImpl);
	
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);			
		}
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9306
	 * @throws Exception
	 */
	public void testAlternativesInBeansXml() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/beansxml/incremental/Test3.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 8);

			IFile beansXml = tckProject.getFile("JavaSource/META-INF/beans.xml");
			IFile emptyBeansXml = tckProject.getFile("JavaSource/META-INF/beans.xml.empty");

			beansXml.setContents(emptyBeansXml.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(beansXml);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 8);

			IFile beansXmlWithAlternative = tckProject.getFile("JavaSource/META-INF/beans.xml.with.alternative");

			beansXml.setContents(beansXmlWithAlternative.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(beansXml);
	
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 8);
		} finally {
			IFile beansXml = tckProject.getFile("JavaSource/META-INF/beans.xml");
			IFile beansXmlWithAlternative = tckProject.getFile("JavaSource/META-INF/beans.xml.with.alternative");
			beansXml.setContents(beansXmlWithAlternative.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(beansXml);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-12503
	 * @throws Exception
	 */
	public void testRestrictedType() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/NewBean.java");
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION[getVersionIndex()], 5);
			IFile interfaceFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/LocalExtendedBean.java");
			IFile modifiedFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/LocalExtendedBean.changed");
			interfaceFile.setContents(modifiedFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(interfaceFile);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION[getVersionIndex()], 5);

			IFile originalFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/LocalExtendedBean.original");
			interfaceFile.setContents(originalFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(interfaceFile);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION[getVersionIndex()], 5);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-12503
	 * @throws Exception
	 */
	public void testInjectionsWithInterface() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/ExtensionManager.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);
			IFile interfaceFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/IExtension.java");
			IFile modifiedFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/IExtension.changed");
			interfaceFile.setContents(modifiedFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(interfaceFile);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);

			IFile originalFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/IExtension.original");
			interfaceFile.setContents(originalFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(interfaceFile);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 7);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public void testPackageInfoJavaFile() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/packageinfo/zoo/Zoo.java");
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
			IFile packageInfoFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/packageinfo/pet/package-info.java");
			IFile packageInfoFileWExclude = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/packageinfo/pet/package-info.changed");
			packageInfoFile.setContents(packageInfoFileWExclude.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(packageInfoFile);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);

			IFile originalFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/packageinfo/pet/package-info.original");
			packageInfoFile.setContents(originalFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(packageInfoFile);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 7);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public void testRemovingType() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile hibernationFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/removingtype/Hibernation.java");
			IFile denFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/removingtype/Den.java");

			//1. First check that we do have an injection to test.
			IInjectionPointField point = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/removingtype/Den.java", "bear");
			assertNotNull(point);
			Collection<IBean> bs = point.getCDIProject().getBeans(false, point);
			assertEquals(bs.size(), 1);

			//2. Next destroy qualifier type.
			String content = FileUtils.readStream(hibernationFile);
			hibernationFile.setContents(new ByteArrayInputStream("".getBytes()), true, false, new NullProgressMonitor());

			//3. Validate after Java build but without CDI build.
			//Exception will be caught by ValidationManager and ValidationExceptionTest
			//will fail reporting it.
			validateAfterJavaBuild(denFile);

			//4. Check that CDI model has not changed but the qualifier declaration cannot find Java type. 
			point = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/removingtype/Den.java", "bear");
			IAnnotationDeclaration d = point.getAnnotation("org.jboss.jsr299.tck.tests.jbt.validation.inject.incremental.removingtype.Hibernation");
			assertNotNull(d); //because CDI builder has not been invoked.
			assertNull(d.getType()); //because Java builder has been invoked.

			//5. direct check of fix of JBIDE-18491
			try {
				CDIProject.getAnnotationDeclarationKey(d);
			} catch (NullPointerException e) {
				TestCase.fail("CDIProject.getAnnotationDeclarationKey fails with obsolete type.");
			}

			//6. Rebuild/revalidate and restore the qualifier type.
			TestUtil.validate(denFile);
			hibernationFile.setContents(new ByteArrayInputStream(content.getBytes()), true, false, new NullProgressMonitor());
			TestUtil.validate(denFile);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public static void validateAfterJavaBuild(IResource resource) throws CoreException {
		ValidationFramework.getDefault().suspendAllValidation(true);
		resource.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, JavaCore.BUILDER_ID, new HashMap<String, String>(), new NullProgressMonitor());
		ValidationFramework.getDefault().suspendAllValidation(false);
		
		try {
			new IncrementalValidatorOperation(resource.getProject(), new Object[]{resource}).run(new NullProgressMonitor());
		} catch (OperationCanceledException e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		} finally {
			ValidationFramework.getDefault().suspendAllValidation(true);
		}
	}

	private static class IncrementalValidatorOperation extends ValidatorSubsetOperation {
		public IncrementalValidatorOperation(IProject project, Object[] changedResources) throws InvocationTargetException {
			super(project, shouldForce(changedResources), RegistryConstants.ATT_RULE_GROUP_DEFAULT, false);
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			setEnabledValidators(InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(true)));
			setFileDeltas(FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, false));
		}
	}
}