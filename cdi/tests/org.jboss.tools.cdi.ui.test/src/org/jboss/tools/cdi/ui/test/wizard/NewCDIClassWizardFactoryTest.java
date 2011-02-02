package org.jboss.tools.cdi.ui.test.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard;
import org.jboss.tools.cdi.xml.ui.editor.form.CDINewClassWizardFactory;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.wizards.INewClassWizard;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

import junit.framework.TestCase;

public class NewCDIClassWizardFactoryTest extends TestCase {
	IProject tck;

	public void testClassWizardFactory() throws Exception {
		tck = ResourcesPlugin.getWorkspace().getRoot().getProject("tck");
		IFile file = tck.getFile("WebContent/WEB-INF/beans.xml");
		assertNotNull(file);
		CDINewClassWizardFactory factory = new CDINewClassWizardFactory();
		XModelObject o = EclipseResourceUtil.createObjectForResource(file);
		assertNotNull(o);
		
		XModelObject decorators = o.getChildByPath("Decorators");
		XModelObject decorator = decorators.getChildByPath("com.acme.NonExistantDecoratorClass");
		assertNotNull(decorator);
		XAttribute clsAttr = decorator.getModelEntity().getAttribute("class");
		assertNotNull(clsAttr);
		INewClassWizard wizard = factory.createWizard(decorators, clsAttr);
		assertTrue(wizard instanceof NewDecoratorCreationWizard);
	
		XModelObject alternatives = o.getChildByPath("Alternatives");
		XModelObject stereotype = alternatives.getChildByPath("org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.Mock");
		assertNotNull(stereotype);
		XAttribute stereotypeAttr = stereotype.getModelEntity().getAttribute("stereotype");
		assertNotNull(stereotypeAttr);
		wizard = factory.createWizard(alternatives, stereotypeAttr);
		assertTrue(wizard instanceof NewStereotypeCreationWizard);
		wizard = factory.createWizard(alternatives, clsAttr);
		assertTrue(wizard instanceof NewBeanCreationWizard);
		
		XModelObject interceptors = o.getChildByPath("Interceptors");
		XModelObject interceptor = interceptors.getChildByPath("com.acme.Foo");
		assertNotNull(interceptor);
		wizard = factory.createWizard(interceptors, clsAttr);
		assertTrue(wizard instanceof NewInterceptorCreationWizard);
		wizard = factory.createWizard(interceptor, clsAttr);
		assertTrue(wizard instanceof NewInterceptorCreationWizard);
		
	}
}
