package org.jboss.tools.cdi.seam.core.test.persistence;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.test.DependentProjectTest;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;

public class SeamPersistenceTest extends TestCase {

	protected IProject project;
	protected IProject dependentProject;

	public IProject getTestProject() throws IOException, CoreException, InvocationTargetException, InterruptedException {
		if(project==null) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(SeamPersistenceTestSetup.PROJECT_NAME);
			if(!project.exists()) {
				project = ResourcesUtils.importProject(SeamPersistenceTestSetup.PLUGIN_ID, SeamPersistenceTestSetup.PROJECT_PATH);
				TestUtil.waitForValidation();
			}
		}
		return project;
	}

	public IProject getDependentTestProject() throws IOException, CoreException, InvocationTargetException, InterruptedException {
		if(dependentProject==null) {
			dependentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(SeamPersistenceTestSetup.DEPENDENT_PROJECT_NAME);
			if(!dependentProject.exists()) {
				dependentProject = ResourcesUtils.importProject(SeamPersistenceTestSetup.PLUGIN_ID, SeamPersistenceTestSetup.DEPENDENT_PROJECT_PATH);
				TestUtil.waitForValidation();
			}
		}
		return dependentProject;
	}

	public void testEntityManagerInjection() throws Exception {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdi, "/src/test/EntityManagerTest.java", "manager");
		assertNotNull(p);

		Set<IBean> bs = cdi.getBeans(false, p);
		assertEquals(1, bs.size());
	}

	public void testEntityManagerInjectionInDependentProject() throws Exception {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getDependentTestProject(), true);
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdi, "/src/test/EntityManager2Test.java", "manager");
		assertNotNull(p);

		Set<IBean> bs = cdi.getBeans(false, p);
		assertEquals(1, bs.size());
	}

	public void testSessionInjection() throws Exception {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdi, "/src/test/EntityManagerTest.java", "session");
		assertNotNull(p);

		Set<IBean> bs = cdi.getBeans(false, p);
		assertFalse(bs.isEmpty());
	}

	public void testSessionInjectionInDependentProject() throws Exception {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getDependentTestProject(), true);
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdi, "/src/test/EntityManager2Test.java", "session");
		assertNotNull(p);

		Set<IBean> bs = cdi.getBeans(false, p);
		assertEquals(1, bs.size());
	}
}
