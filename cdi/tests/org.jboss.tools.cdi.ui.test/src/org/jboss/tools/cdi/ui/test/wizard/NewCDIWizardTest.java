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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
//import org.jboss.tools.cdi.ui.wizard.NewCDIAnnotationWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewDecoratorWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorBindingWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewQualifierWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewScopeWizardPage;
import org.jboss.tools.cdi.ui.wizard.NewStereotypeWizardPage;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Viacheslav Kabanovich
 *
 */
public class NewCDIWizardTest extends TestCase {
	
	static String PACK_NAME = "test";
	static String QUALIFIER_NAME = "MyQualifier";
	static String STEREOTYPE_NAME = "MyStereotype";
	static String SCOPE_NAME = "MyScope";
	static String INTERCEPTOR_BINDING_NAME = "MyInterceptorBinding";
	static String INTERCEPTOR_NAME = "MyInterceptor";
	static String DECORATOR_NAME = "MapDecorator<K,V>";
	
	static class WizardContext {
		NewElementWizard wizard;
		IProject tck;
		IJavaProject jp;
		WizardDialog dialog;
		NewTypeWizardPage page;
		String packName;
		String typeName;
		

		public void init(String wizardId, String packName, String typeName) {
			this.packName = packName;
			this.typeName = typeName;
			wizard = (NewElementWizard)WorkbenchUtils.findWizardByDefId(wizardId);
			tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
			jp = EclipseUtil.getJavaProject(tck);
			wizard.init(CDIUIPlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
			dialog = new WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			dialog.setBlockOnOpen(false);
			dialog.open();

			page = (NewTypeWizardPage)dialog.getSelectedPage();

			page.setTypeName(typeName, true);
			IPackageFragment pack = page.getPackageFragmentRoot().getPackageFragment(PACK_NAME);
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
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@Stereotype"));
			assertTrue(text.contains("@Inherited"));
			assertTrue(text.contains("@Named"));
			assertTrue(text.contains("@Target({ METHOD, FIELD })"));
			assertTrue(text.contains("@Retention(RUNTIME)"));
			
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
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@InterceptorBinding"));
			assertTrue(text.contains("@Inherited"));
			assertTrue(text.contains("@Target({ TYPE, METHOD })"));
			assertTrue(text.contains("@Retention(RUNTIME)"));
			
		} finally {
			context.close();
		}
	}

	public void testNewInterceptorWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard",
				PACK_NAME, INTERCEPTOR_NAME);JobUtils.waitForIdle(2000);
		JobUtils.waitForIdle(2000);
		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		ICDIAnnotation a = cdi.getInterceptorBinding(PACK_NAME + "." + INTERCEPTOR_BINDING_NAME);
		
		try {
			NewInterceptorWizardPage page = (NewInterceptorWizardPage)context.page;
			
			page.addInterceptorBinding(a);
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			
			assertTrue(text.contains("@Interceptor"));
			assertTrue(text.contains("@" + INTERCEPTOR_BINDING_NAME));
			
		} finally {
			context.close();
		}
	}

	public void testNewDecoratorWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard",
				PACK_NAME, DECORATOR_NAME);JobUtils.waitForIdle(2000);
		JobUtils.waitForIdle(2000);
		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewDecoratorWizardPage page = (NewDecoratorWizardPage)context.page;
			
			List<String> interfacesNames = new ArrayList<String>();
			interfacesNames.add("java.util.Map<K,V>");
			page.setSuperInterfaces(interfacesNames, true);
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			System.out.println(text);
			
			assertTrue(text.contains("@Decorator"));
			assertTrue(text.contains("@Delegate"));
		} finally {
			context.close();
		}
	}

	public void testNewAnnotationLiteralWizard() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard",
				PACK_NAME, QUALIFIER_NAME + "Literal");JobUtils.waitForIdle(2000);
		JobUtils.waitForIdle(2000);
		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewAnnotationLiteralWizardPage page = (NewAnnotationLiteralWizardPage)context.page;

			page.setQualifier(PACK_NAME + "." + QUALIFIER_NAME);
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			System.out.println(text);
			
			assertTrue(text.contains("AnnotationLiteral<" + QUALIFIER_NAME + ">"));
		} finally {
			context.close();
		}
	}

	public void testNewAnnotationLiteralWizardWithMembers() {
		WizardContext context = new WizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewAnnotationLiteralCreationWizard",
				PACK_NAME, "NewLiteral");JobUtils.waitForIdle(2000);
		JobUtils.waitForIdle(2000);
		ICDIProject cdi = CDICorePlugin.getCDIProject(context.tck, true);
		
		try {
			NewAnnotationLiteralWizardPage page = (NewAnnotationLiteralWizardPage)context.page;
			
			page.setQualifier("javax.enterprise.inject.New");
			
			context.wizard.performFinish();
			
			String text = context.getNewTypeContent();
			System.out.println(text);
			
			assertTrue(text.contains("AnnotationLiteral<New>"));
			assertTrue(text.contains("private final Class<?> value;"));
		} finally {
			context.close();
		}
	}

	public void testNewBeansXMLWizard() throws CoreException {
		NewBeansXMLWizardContext context = new NewBeansXMLWizardContext();
		context.init("org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard");
		JobUtils.waitForIdle(2000);
		
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