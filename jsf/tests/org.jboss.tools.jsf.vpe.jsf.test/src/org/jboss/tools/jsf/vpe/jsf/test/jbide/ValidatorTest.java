/**
 * 
 */
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.io.IOException;
import java.util.Date;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.util.VpeDebugUtil;
import org.jboss.tools.vpe.ui.test.ProjectsLoader;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * 
 * Simple half-automatic validator test
 * @author mareshkau
 *
 */
public class ValidatorTest extends VpeTest{

	public ValidatorTest(String name) {
		super(name);
	}
	
	public void testValidationTime() throws IOException, CoreException{
		IProject project = ProjectsLoader.getInstance().getProject(JsfAllTests.IMPORT_PROJECT_NAME);
		//wait wile import project job finished
		TestUtil.waitForIdle(2000);
		long startValidationTime = (new Date()).getTime();
		ValidationFramework.getDefault().validate(new IProject[] { project },
				false, false, new NullProgressMonitor());
		//wait while validation job starts
		TestUtil.delay(200);
		TestUtil.waitForJobs();
		VpeDebugUtil.debugInfo("Validation time was "+((new Date()).getTime()-startValidationTime)/1000.0+"sec");  //$NON-NLS-1$//$NON-NLS-2$
	}

}
