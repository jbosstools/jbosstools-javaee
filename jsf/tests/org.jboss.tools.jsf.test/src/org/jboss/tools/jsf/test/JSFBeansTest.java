package org.jboss.tools.jsf.test;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

import junit.framework.TestCase;

public class JSFBeansTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = true;

	public JSFBeansTest() {}

	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.test", null, "JSFKickStart1", false); 
		project = provider.getProject();
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
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

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

}
