/**
 * 
 */
package org.jboss.tools.seam.core.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.xpl.EditorTestHelper;

import junit.extensions.TestSetup;
import junit.framework.Test;

/**
 * @author dgolovin
 *
 */
public class SeamValidatorsTestSetup extends TestSetup {

	public SeamValidatorsTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		ResourcesUtils.importProject("org.jboss.tools.seam.core.test","projects/SeamWebWarTestProject" , new NullProgressMonitor());
		EditorTestHelper.joinBackgroundActivities();		
	}

	@Override
	protected void tearDown() throws Exception {
		EditorTestHelper.joinBackgroundActivities();		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("SeamWebWarTestProject");
		if (project != null) {
			project.delete(true, true, null);
		}
	}
	
}
