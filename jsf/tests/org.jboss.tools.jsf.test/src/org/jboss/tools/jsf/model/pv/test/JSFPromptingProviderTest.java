package org.jboss.tools.jsf.model.pv.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFPromptingProvider;
import org.jboss.tools.jsf.plugin.JsfTestPlugin;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.test.util.JobUtils;
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
		assertTrue(provider.isSupporting(IWebPromptingProvider.JSF_BUNDLES));
	}

	public void testGetList() {
		List<Object> list = provider.getList(model, "", "", new Properties());
		assertEquals("Empty list should be returned for unsupported id.",IWebPromptingProvider.EMPTY_LIST, list);
	}

	/******************************************************************
	 * getBundles method test
	 ******************************************************************/
	public static final int GET_BUNDLE_EXPECTED_LIST_SIZE = 1;
	/**
	 * 
	 */
	public void testGetBundles() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_BUNDLES, "", new Properties());
		assertEquals("Bundles proposal list has wrong size",GET_BUNDLE_EXPECTED_LIST_SIZE, list.size());
	}

	public static final int GET_BUNDLE_PROPERTIES_EXPECTED_LIST_SIZE = 3;

	public void testGetBundleProperties() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_BUNDLE_PROPERTIES, "org.jboss.tools.jsf.test.Bundle", new Properties());
		assertEquals("Bundles properties proposal list has wrong size",GET_BUNDLE_PROPERTIES_EXPECTED_LIST_SIZE, list.size());
	}

	/**
	 * Expected beans list size
	 */
	public static final int GET_BEANS_EXPECTED_LIST_SIZE = 12;
	/**
	 * <code>JSFPromptingProvider.getBundles()</code> method test
	 */
	public void testGetBeans() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_MANAGED_BEANS, null, new Properties());
		assertEquals("Managed Beans proposal list has wrong size",GET_BEANS_EXPECTED_LIST_SIZE, list.size());
	}

	public static final int GET_BEAN_PROPERTIES_LIST_SIZE = 5;

	public void testGetBeanProperties() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_BEAN_PROPERTIES, "mbean1.", new Properties());
		assertEquals("Bean properties proposal list has wrong size",GET_BEAN_PROPERTIES_LIST_SIZE, list.size());
	}

	public static final int BUILD_BEAN_PROPERTIES_LIST_SIZE = 5;

	public void testBuildBeanProperties() {
		List list = provider.buildBeanProperties(model, "org.jboss.tools.jsf.test.ManagedBean1", null);
		assertEquals("Bean properties proposal list has wrong size",BUILD_BEAN_PROPERTIES_LIST_SIZE, list.size());
	}

	public void testGetBeanMethods() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_BEAN_METHODS, "mbean2", new Properties());
		assertEquals(1, list.size());
	}

	public void testGetViewActions() {
		Properties p = new Properties();
		p.setProperty(IWebPromptingProvider.VIEW_PATH, "/pages/inputname.jsp");
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_VIEW_ACTIONS, "", p);
		assertEquals(1, list.size());
	}

	public void testGetPath() {
		List<Object> list = provider.getList(model, IWebPromptingProvider.JSF_GET_PATH, "/a.jsf", new Properties());
		/*
		 * After fixing https://jira.jboss.org/jira/browse/JBIDE-5577
		 * there are two files in the list: .jsp and .xhtml.
		 */
		assertEquals(3, list.size());
		String s = (String)list.get(0);
		assertEquals("/a.jsp", s);
		s = (String)list.get(1);
		assertEquals("/a.xhtml", s);
		s = (String)list.get(2);
		assertEquals("/a.jspx", s);
	}
	
	public void testGetTaglibs() {
		WebProject p = WebProject.getInstance(model);
		try {
			Thread.sleep(5000);
		}  catch (InterruptedException e) {}
		p.getTaglibMapping().invalidate();
		Map<String,XModelObject> map = p.getTaglibMapping().getTaglibObjects();
		XModelObject o1 = map.get("facelet_taglib");
		assertNotNull(o1);
		XModelObject o2 = map.get("facelet_taglib2");
		assertNotNull(o2);
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
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			JobUtils.waitForIdle();
			ResourcesUtils.deleteProject(TEST_PROJECT_NAME);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}
}
