package org.jboss.tools.jsf.test;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

public class JSFBeansTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = true;

	public JSFBeansTest() {}

	public void setUp() throws CoreException {
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("JSFKickStartOldFormat");
		if(project==null) {
			provider = new TestProjectProvider("org.jboss.tools.jsf.test", null, "JSFKickStartOldFormat", false);
			project = provider.getProject();
		}
	}
	
	public void testBeanWithSuper() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		assertNotNull("Test project " + project.getName() + " has no model nature.", n);
		assertNotNull("XModel for project " + project.getName() + " is not loaded.", n.getModel());
		List<Object> result = WebPromptingProvider.getInstance().getList(n.getModel(), IWebPromptingProvider.JSF_BEAN_PROPERTIES, "user.", new Properties());
		assertNotNull("No results for bean " + " user.", n.getModel());
		
		assertTrue("Property 'parent' inherited from super class is not found in bean 'user'", result.contains("parent"));
		
	}
	
	public void testGettersAndSetters() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		List<Object> result = WebPromptingProvider.getInstance().getList(n.getModel(), IWebPromptingProvider.JSF_BEAN_METHODS, "user.", new Properties());
		assertTrue("Method getX1 is not found. It is not a getter because it has type void.", result.contains("getX1"));
		assertTrue("Method getX2 is not found. It is not a getter because it has a parameter.", result.contains("getX2"));
		assertTrue("Method setX3 is not found. It is not a setter because it has 2 parameters", result.contains("setX3"));
	}

	protected void tearDown() throws CoreException{
		if(provider!=null) {
			provider.dispose();
			provider = null;
		}
	}

}
