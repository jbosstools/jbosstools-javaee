package org.jboss.tools.cdi.ui.test.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard;
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
		XAttribute a = decorator.getModelEntity().getAttribute("class");
		assertNotNull(a);
		INewClassWizard wizard = factory.createWizard(decorators, a);
		assertTrue(wizard instanceof NewDecoratorCreationWizard);
		
		
	}
}
