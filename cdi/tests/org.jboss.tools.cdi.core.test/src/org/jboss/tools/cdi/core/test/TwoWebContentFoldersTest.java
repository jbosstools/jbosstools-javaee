package org.jboss.tools.cdi.core.test;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class TwoWebContentFoldersTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;

	public TwoWebContentFoldersTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/TwoWebContentFolders");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public void testBeansXMLInDifferentWebContentFolders() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		Set<IBean> bs = cdi.getBeans("bean1", false);
		assertEquals(1, bs.size());
		assertTrue(bs.iterator().next().isSelectedAlternative());
		
		bs = cdi.getBeans("bean2", false);
		assertEquals(1, bs.size());
		assertTrue(bs.iterator().next().isSelectedAlternative());
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}