/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.test.wizard;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.internal.core.scanner.lib.BeanArchiveDetector;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewBeanWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewCDIElementWizard;
import org.jboss.tools.cdi.ui.wizard.NewDecoratorWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorBindingWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewQualifierWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewScopeWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewStereotypeWizardPage;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Viacheslav Kabanovich
 *
 */
public class NewCDIWizardTest extends TestCase {
	
	static String PACK_NAME = "test";
	static String QUALIFIER_NAME = "MyQualifier";
	static String HAIRY_PACK_NAME = "org.jboss.jsr299.tck.tests.definition.qualifier";
	static String HAIRY_QUALIFIER = "Hairy";
	static String STEREOTYPE_NAME = "MyStereotype";
	static String STEREOTYPE2_NAME = "MyStereotype2";
	static String SCOPE_NAME = "MyScope";
	static String INTERCEPTOR_BINDING_NAME = "MyInterceptorBinding";
	static String INTERCEPTOR_BINDING2_NAME = "MyInterceptorBinding2";
	static String EXISTING_PACK_NAME = "org.jboss.jsr299.tck.tests.jbt.validation.target";
	static String EXISTING_INTERCEPTOR_BINDING_NAME = "InterceptorBindingWTypeTarget";  // @Inherited @Target({TYPE})
	static String INTERCEPTOR_NAME = "MyInterceptor";
	static String DECORATOR_NAME = "MapDecorator<K,V>";
	static String BEAN_NAME = "MyBean";
	
	static class WizardContext {
		NewElementWizard wizard;
		IProject tck;
		IJavaProject jp;
		WizardDialog dialog;
		NewTypeWizardPage page;
		String packName;
		String typeName;
		

		public void init(String wizardId, String packName, String typeName) {
			wizard = (NewElementWizard)WorkbenchUtils.findWizardByDefId(wizardId);
			tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
			jp = EclipseUtil.getJavaProject(tck);
			wizard.init(CDIUIPlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
			if(wizard instanceof NewCDIElementWizard) {
			    ((NewCDIElementWizard)wizard).setOpenEditorAfterFinish(false);
			} else if(wizard instanceof NewAnnotationLiteralCreationWizard) {
				((NewAnnotationLiteralCreationWizard)wizard).setOpenEditorAfterFinish(false);
			}
			dialog = new WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			dialog.setBlockOnOpen(false);
			dialog.open();

			page = (NewTypeWizardPage)dialog.getSelectedPage();

			setTypeName(packName, typeName);
		}

		public void setTypeName(String packName, String typeName) {
			this.packName = packName;
			this.typeName = typeName;
			page.setTypeName(typeName, true);
			IPackageFragment pack = page.getPackageFragmentRoot().getPackageFragment(packName);
			page.setPackageFragment(pack, true);
		}

		public String getNewTypeContent() {
			IType type = null;
			try {
				String tn = typeName;
				int q = tn.indexOf("<");
				if(q >= 0) tn = tn.substring(0, q);
				type = jp.findType(packName + "." + tn);
			} catch (JavaModelException e) {
				JUnitUtils.fail("Cannot find type " + typeName, e);
			}
			
			IFile file = (IFile)type.getResource();
			assertNotNull(file);
			String text = null;
			try {
				text = FileUtil.readStream(file.getContents());
			} catch (CoreException e) {
				JUnitUtils.fail("Cannot read from " + file, e);
			}
			return text;
		}

		public void close() {
			dialog.close();
		}
		
	}

	static class NewBeansXMLWizardContext {
		NewBeansXMLCreationWizard wizard;
		IProject tck;
		IJavaProject jp;
		WizardDialog dialog;
		

		public void init(String wizardId) {
			wizard = (NewBeansXMLCreationWizard)WorkbenchUtils.findWizardByDefId(wizardId);
			tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
			jp = EclipseUtil.getJavaProject(tck);
			wizard.init(CDIUIPlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
			dialog = new WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			wizard.setOpenEditorAfterFinish(false);
			dialog.setBlockOnOpen(false);
			dialog.open();
		}

		public void close() {
			dialog.close();
		}
	}

	public void testNewQualifierWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewQualifierCreationWizard",
				PACK_NAME, QUALIFIER_NAME);

		try {
			NewQualifierWizardPage page = (NewQualifierWizardPage)context.page;
			page.setInherited(true);
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@Qualifier"));
			assertTrue(text.contains("@Inherited"));
			assertTrue(text.contains("@Target({ TYPE, METHOD, PARAMETER, FIELD })"));
			assertTrue(text.contains("@Retention(RUNTIME)"));
			
		} finally {
			context.close();
		}
	}

	public void testNewStereotypeWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard",
				PACK_NAME, STEREOTYPE_NAME);

		try {
			NewStereotypeWizardPage page = (NewStereotypeWizardPage)context.page;
			page.setInherited(true);
			page.setTarget("METHOD,FIELD");
			page.setNamed(true);
			page.setAlternative(true);
			page.setToBeRegisteredInBeansXML(true);
			
			assertTrue(page.isToBeRegisteredInBeansXML());
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@Stereotype"));
			assertTrue(text.contains("@Inherited"));
			assertTrue(text.contains("@Named"));
			assertTrue(text.contains("@Target({ METHOD, FIELD })"));
			assertTrue(text.contains("@Retention(RUNTIME)"));

			IProject tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
			IFile f = tck.getFile("WebContent/WEB-INF/beans.xml");
			XModelObject o = EclipseResourceUtil.createObjectForResource(f);
			XModelObject c = o.getChildByPath("Alternatives/" + PACK_NAME + "." + STEREOTYPE_NAME);
			assertNotNull(c);
			
		} finally {
			context.close();
		}

		// testNewStereotypeWizardWithStereotype()
		context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard",
				PACK_NAME, STEREOTYPE2_NAME);
		try {
			context.tck.build(IncrementalProjectBuilder.INCREMENTAL_BUILD,null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		IStereotype s = cdi.getStereotype(PACK_NAME + "." + STEREOTYPE_NAME);
		IStereotype d = cdi.getStereotype(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
		assertNotNull(s);
		assertNotNull(d);
		
		try {
			NewStereotypeWizardPage page = (NewStereotypeWizardPage)context.page;
			page.setInherited(true);
			page.setTarget("METHOD,FIELD");
			page.setNamed(true);
			
			page.addStereotype(d);
			String message = page.getErrorMessage();
			assertNull(message);
			message = page.getMessage();
			assertNotNull(message);
			int messageType = page.getMessageType();
			assertEquals(IMessageProvider.WARNING, messageType);
			String testmessage = NLS.bind(CDIUIMessages.MESSAGE_STEREOTYPE_IS_NOT_COMPATIBLE, d.getSourceType().getElementName());
			assertEquals(testmessage, message);

			page.addStereotype(s);
			message = page.getErrorMessage();
			testmessage = NLS.bind(CDIUIMessages.MESSAGE_STEREOTYPE_CANNOT_BE_APPLIED_TO_TYPE, s.getSourceType().getElementName());
			assertEquals(testmessage, message);
		} finally {
			context.close();
		}

	}

	public void testNewScopeWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewScopeCreationWizard",
				PACK_NAME, SCOPE_NAME);

		try {
			NewScopeWizardPage page = (NewScopeWizardPage)context.page;
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@NormalScope"));
			assertTrue(text.contains("@Inherited"));
			assertTrue(text.contains("@Target({ TYPE, METHOD, FIELD })"));
			assertTrue(text.contains("@Retention(RUNTIME)"));
			
		} finally {
			context.close();
		}
	}

	public void testNewInterceptorBindingWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewInterceptorBindingCreationWizard",
				PACK_NAME, INTERCEPTOR_BINDING_NAME);

		try {
			NewInterceptorBindingWizardPage page = (NewInterceptorBindingWizardPage)context.page;
			page.setTarget("TYPE");
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@InterceptorBinding"));
			assertTrue(text.contains("@Inherited"));
			assertTrue(text.contains("@Target({ TYPE })"));
			assertTrue(text.contains("@Retention(RUNTIME)"));
			
		} finally {
			context.close();
		}
	}

	/**
	 * Existing interceptor binding, taken from TCK, is annotated @Inherited @Target({TYPE})
	 * If this test fails, first check that the existing interceptor binding has not been 
	 * moved or modified.
	 * In the previous version, the result of testNewInterceptorBindingWizard() was used, 
	 * but that turned to be not safe, since the order of tests is not guaranteed.
	 * 
	 * @throws CoreException
	 */
	public void testNewInterceptorBindingWizardWithBinding() throws CoreException {
		IProject tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
		tck.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewInterceptorBindingCreationWizard",
				PACK_NAME, INTERCEPTOR_BINDING2_NAME);


		try {
			NewInterceptorBindingWizardPage page = (NewInterceptorBindingWizardPage)context.page;
			ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
			IInterceptorBinding s = cdi.getInterceptorBinding(EXISTING_PACK_NAME + "." + EXISTING_INTERCEPTOR_BINDING_NAME);
			assertNotNull(s);
			
			page.addInterceptorBinding(s);
			String message = page.getErrorMessage();
			assertNull(message);
			message = page.getMessage();
			assertNotNull(message);
			int messageType = page.getMessageType();
			assertEquals(IMessageProvider.WARNING, messageType);
			String testmessage = NLS.bind(CDIUIMessages.MESSAGE_INTERCEPTOR_BINDING_IS_NOT_COMPATIBLE, s.getSourceType().getElementName());
			assertEquals(testmessage, message);
			
			page.setTarget("TYPE");
			
			message = page.getErrorMessage();
			assertNull(message);
			message = page.getMessage();
			assertNull(message);
			
		} finally {
			context.close();
		}
	}

	/**
	 * Existing interceptor binding, taken from TCK, is annotated @Inherited @Target({TYPE})
	 * If this test fails, first check that the existing interceptor binding has not been 
	 * moved or modified.
	 * In the previous version, the result of testNewInterceptorBindingWizard() was used, 
	 * but that turned to be not safe, since the order of tests is not guaranteed.
	 * 
	 * @throws CoreException
	 */
	public void testNewInterceptorWizard() throws CoreException {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard",
				PACK_NAME, INTERCEPTOR_NAME);

		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		ICDIAnnotation a = cdi.getInterceptorBinding(EXISTING_PACK_NAME + "." + EXISTING_INTERCEPTOR_BINDING_NAME);
		assertNotNull(a);
		
		try {
			NewInterceptorWizardPage page = (NewInterceptorWizardPage)context.page;
			
			page.addInterceptorBinding(a);
			
			assertTrue(page.isToBeRegisteredInBeansXML());
			context.setTypeName("com.acme", "Foo");
			assertFalse(page.isToBeRegisteredInBeansXML());
			context.setTypeName(PACK_NAME, INTERCEPTOR_NAME);
			assertTrue(page.isToBeRegisteredInBeansXML());

			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@Interceptor"));
			assertTrue(text.contains("@" + EXISTING_INTERCEPTOR_BINDING_NAME));
			
			IProject tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
			IFile f = tck.getFile("WebContent/WEB-INF/beans.xml");
			XModelObject o = EclipseResourceUtil.createObjectForResource(f);
			XModelObject c = o.getChildByPath("Interceptors/" + PACK_NAME + "." + INTERCEPTOR_NAME);
			assertNotNull(c);
		} finally {
			context.close();
		}
	}

	public void testNewDecoratorWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard",
				PACK_NAME, DECORATOR_NAME);

		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewDecoratorWizardPage page = (NewDecoratorWizardPage)context.page;
			
			List<String> interfacesNames = new ArrayList<String>();
			interfacesNames.add("java.util.Map<K,V>");
			page.setSuperInterfaces(interfacesNames, true);
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
//			System.out.println(text);
			
			assertTrue(text.contains("@Decorator"));
			assertTrue(text.contains("@Delegate"));
		} finally {
			context.close();
		}
	}

	public void testNewBeanWizard() throws Exception {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard",
				PACK_NAME, BEAN_NAME);

		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewBeanWizardPage page = (NewBeanWizardPage)context.page;
			
			page.setBeanName("myNewBean");

			assertEquals(IMessageProvider.NONE, page.getMessageType());			

			page.setScope(CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

			String message = page.getMessage();
			assertEquals(CDIUIMessages.MESSAGE_BEAN_SHOULD_BE_SERIALIZABLE, message);
			assertEquals(IMessageProvider.WARNING, page.getMessageType());

			page.setScope(CDIConstants.APPLICATION_SCOPED_ANNOTATION_TYPE_NAME);
			assertEquals(IMessageProvider.NONE, page.getMessageType());			

			cdi.getNature().setBeanDiscoveryMode(BeanArchiveDetector.ANNOTATED);
			page.setScope("");
			message = page.getErrorMessage();
			assertEquals(CDIUIMessages.SCOPE_SHOULD_BE_SET_IN_ARCHIVE_WITH_DISCOVERY_MODE_ANNOTATED, message);

			page.setScope(CDIConstants.APPLICATION_SCOPED_ANNOTATION_TYPE_NAME);
			assertEquals(IMessageProvider.NONE, page.getMessageType());
			cdi.getNature().setBeanDiscoveryMode(BeanArchiveDetector.ALL);
			page.setScope("");
			assertEquals(IMessageProvider.NONE, page.getMessageType());

			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
//			System.out.println(text);
			
			assertTrue(text.contains("@Named"));
			assertTrue(text.contains("\"myNewBean\""));
			
			IType type = (IType)context.wizard.getCreatedElement();
			int f = type.getFlags();
			assertTrue(Modifier.isPublic(f));
			assertFalse(Modifier.isAbstract(f));
//			String[] is = type.getSuperInterfaceNames();
//			assertEquals(1, is.length);
//			assertEquals("Serializable", is[0]);
		} finally {
			context.close();
		}
	}

	public void testNewAnnotationLiteralWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard",
				PACK_NAME, HAIRY_QUALIFIER + "Literal");

		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewAnnotationLiteralWizardPage page = (NewAnnotationLiteralWizardPage)context.page;

			page.setQualifier(HAIRY_PACK_NAME + "." + HAIRY_QUALIFIER);
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
//			System.out.println(text);
			
			assertTrue(text.contains("AnnotationLiteral<" + HAIRY_QUALIFIER + ">"));
		} finally {
			context.close();
		}
	}

	public void testNewAnnotationLiteralWizardWithMembers() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard",
				PACK_NAME, "NewLiteral");

		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewAnnotationLiteralWizardPage page = (NewAnnotationLiteralWizardPage)context.page;
			
			page.setQualifier("javax.enterprise.inject.New");
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
//			System.out.println(text);
			
			assertTrue(text.contains("AnnotationLiteral<New>"));
			assertTrue(text.contains("private final Class<?> value;"));
		} finally {
			context.close();
		}
	}

	public void testNewBeansXMLWizard() throws CoreException {
		NewBeansXMLWizardContext context = new NewBeansXMLWizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard");
		
		try {
			
			WizardNewFileCreationPage page = (WizardNewFileCreationPage)context.wizard.getPage("newFilePage1");
			String s = page.getFileName();
			assertEquals("beans.xml", s);
			assertFalse(context.wizard.canFinish());
			page.setFileName("beans2.xml");
			assertTrue(context.wizard.canFinish());
			String c = page.getContainerFullPath().toString();
			assertEquals("/tck/WebContent/WEB-INF", c);
			
			context.wizard.performFinish();
		
			IFile f = context.tck.getParent().getFile(page.getContainerFullPath().append(page.getFileName()));
			assertTrue(f.exists());
			
			String text = FileUtil.readStream(f.getContents());
			assertTrue(text.indexOf("http://java.sun.com/xml/ns/javaee") > 0);

		} finally {
			context.close();
		}
	}
}