package org.jboss.tools.jsf.model.pv.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFPromptingProvider;
import org.jboss.tools.jsf.plugin.JsfTestPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.test.util.ResourcesUtils;

import junit.framework.TestCase;


public class JSFPromptingProviderTest extends TestCase {
	
	private static final String TEST_PROJECT_NAME = "JSFPromptingProviderTestProject";
	
	private static final String TEST_PROJECT_PATH = "/projects/" + TEST_PROJECT_NAME;

	
	IProject project = null;
	IModelNature nature = null;
	XModel model = null;
	
	JSFPromptingProvider provider = null;
	
	
	@Override
	protected void setUp() throws IOException, CoreException, InvocationTargetException, InterruptedException {
//		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(TEST_PROJECT_NAME);
//		if(project==null) {
		project = ResourcesUtils.importProject(
				JsfTestPlugin.getDefault().getBundle(), TEST_PROJECT_PATH);
		assertNotNull(project);
//		}
		
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		nature = EclipseResourceUtil.getModelNature(project);
		assertNotNull(nature);
		model = nature.getModel();
		assertNotNull(model);
		provider = new JSFPromptingProvider();

	}
	
	public void testIsSupporting() {
		fail("Not yet implemented");
	}

	public void testGetList() {
		fail("Not yet implemented");
	}

	/******************************************************************
	 * getBundles method test
	 ******************************************************************/
	public static final int GET_BUNDLE_EXPECTED_LIST_SIZE = 0;
	/**
	 * 
	 */
	public void testGetBundles() {
		List<Object> list = provider.getBundles(model);
		assertEquals("Bundles proposal list has wrong size",GET_BUNDLE_EXPECTED_LIST_SIZE, list.size());
	}

	public void testGetBundleProperties() {
		fail("Not yet implemented");
	}

	/**
	 * Expected beans list size
	 */
	public static final int GET_BEANS_EXPECTED_LIST_SIZE = 10;
	/**
	 * <code>JSFPromptingProvider.getBundles()</code> method test
	 */
	public void testGetBeans() {
		List<Object> list = provider.getBeans(model);
		assertEquals("Managed Beans proposal list has wrong size",GET_BEANS_EXPECTED_LIST_SIZE, list.size());
		
	}

	public void testGetBeanProperties() {
		fail("Not yet implemented");
	}

	public void testBuildBeanProperties() {
		fail("Not yet implemented");
	}

	public void testBuildBean() {
		fail("Not yet implemented");
	}

	public void testFindBean() {
		fail("Not yet implemented");
	}

	public void testFindBeanClass() {
		fail("Not yet implemented");
	}

	public void testFindBeanClassByClassName() {
		fail("Not yet implemented");
	}

	public void testGetBeanMethods() {
		fail("Not yet implemented");
	}

	public void testGetViewActions() {
		fail("Not yet implemented");
	}

	public void testGetPathAsList() {
		fail("Not yet implemented");
	}

	public void testGetPath() {
		fail("Not yet implemented");
	}
	
	public void testOpenBean() {
		List<Object> list = provider.getBeans(model);
		for (Object object : list) {
			provider.getList(model, WebPromptingProvider.JSF_BEAN_OPEN, object.toString(), null);
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			assertNotNull(editor);
			assertTrue(editor.getClass().getName().endsWith("CompilationUnitEditor"));
			// TODO check that right java class opened
		}
			
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(project!=null) {
			ResourcesUtils.deleteProject(TEST_PROJECT_NAME);
		}
	}
}
