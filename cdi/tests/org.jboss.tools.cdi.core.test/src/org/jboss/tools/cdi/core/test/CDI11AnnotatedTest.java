package org.jboss.tools.cdi.core.test;

import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.CDIProjectAsYouType;
import org.jboss.tools.test.util.WorkbenchUtils;

public class CDI11AnnotatedTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;
	ICDIProject cdiProject;
	String fileName = "src/test/a/Test.java";

	public CDI11AnnotatedTest() {}

	@Override
	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest11");
		cdiProject = CDICorePlugin.getCDIProject(project, true);
	}


	/**
	 * Class declared in file has scope annotation.
	 * @throws Exception
	 */
	public void testModelA() throws Exception {
		IFile f = project.getFile("src/test/a/BeanA.java");
		assertTrue(f.exists());
		
		IEditorPart editorPart = WorkbenchUtils.openEditor(f.getFullPath());
		assertNotNull(editorPart);

		try {
			CDIProjectAsYouType ayt = new CDIProjectAsYouType(cdiProject, f);
			Collection<IBean> bs = ayt.getBeans(f.getFullPath());
			assertEquals(1, bs.size());
		} finally {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);
		}
	}

	/**
	 * Class declared in file has no scope annotation.
	 * @throws Exception
	 */
	public void testModelB() throws Exception {
		IFile f = project.getFile("src/test/a/BeanB.java");
		assertTrue(f.exists());
		
		IEditorPart editorPart = WorkbenchUtils.openEditor(f.getFullPath());
		assertNotNull(editorPart);

		try {
			CDIProjectAsYouType ayt = new CDIProjectAsYouType(cdiProject, f);
			Collection<IBean> bs = ayt.getBeans(f.getFullPath());
			assertEquals(0, bs.size());
		} finally {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);
		}
	}

	/**
	 * File contains two types, one of which has scope annotation.
	 * @throws Exception
	 */
	public void testModelC() throws Exception {
		IFile f = project.getFile("src/test/a/BeanC.java");
		assertTrue(f.exists());
		
		IEditorPart editorPart = WorkbenchUtils.openEditor(f.getFullPath());
		assertNotNull(editorPart);

		try {
			CDIProjectAsYouType ayt = new CDIProjectAsYouType(cdiProject, f);
			Collection<IBean> bs = ayt.getBeans(f.getFullPath());
			assertEquals(1, bs.size());
		} finally {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);
		}
	}
}
