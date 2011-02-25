package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.operation.JSFProjectCreationOperation;
import org.jboss.tools.jsf.web.helpers.context.NewProjectWizardContext;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

import junit.framework.TestCase;

public class NewJSFProjectTest extends TestCase {
	static String PROJECT_NAME = "NewTestProject";
	
	public void testNewJSFProjectOperation() throws Exception {
		NewProjectWizardContext context = new NewProjectWizardContext();
		IProject project = getProjectHandle();
		context.setProject(project);
		IPath defaultPath = ModelUIPlugin.getWorkspace().getRoot().getLocation();
		IPath locationPath = defaultPath.append(PROJECT_NAME);
		context.setServletVersion("2.5");
		context.setProjectLocation(locationPath.toOSString());
		context.setProjectTemplate("JSFKickStartWithoutLibs");
		context.setJSFVersion("JSF 1.2");

		JSFProjectCreationOperation operation = new JSFProjectCreationOperation(context);
		IWorkbenchWindow window = JsfUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		window.run(false, false, operation);
		
		IModelNature nature = EclipseResourceUtil.getModelNature(project);
		assertNotNull(nature);
		XModelObject webxml = nature.getModel().getByPath("/web.xml");
		assertNotNull(webxml);
		
		XModelObject[] s = WebAppHelper.getServlets(webxml);
		assertTrue(s.length > 0);
		String servletName = s[0].getAttributeValue("servlet-name");
		assertEquals("Faces Servlet", servletName);
		
		XModelObject facesConfig = nature.getModel().getByPath("/faces-config.xml");
		assertNotNull(facesConfig);
		XModelObject userBean = facesConfig.getChildByPath("Managed Beans/user");
		assertNotNull(userBean);
	}

	private IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}
	
}
