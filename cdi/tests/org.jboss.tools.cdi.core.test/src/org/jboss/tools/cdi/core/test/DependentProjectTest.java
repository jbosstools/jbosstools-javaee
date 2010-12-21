package org.jboss.tools.cdi.core.test;


import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class DependentProjectTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;
	IProject project2 = null;

	public DependentProjectTest() {}

	public void setUp() throws Exception {
		project1 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest1");
		project1.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();

		project2 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest2");
		project2.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();		
	}

	public void testDependentProject() throws CoreException, IOException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);
	}

	void assertBeanIsPresent(ICDIProject cdi2, String beanClass, boolean present) {
		IBean[] beans = cdi2.getBeans();
		IClassBean cb = null;
		for (IBean b: beans) {
			if(b instanceof IClassBean) {
				IClassBean cb1 = (IClassBean)b;
				if(beanClass.equals(cb1.getBeanClass().getFullyQualifiedName())) {
					cb = cb1;
				}
			}
		}
		if(present) {
			assertNotNull(cb);
		} else {
			assertNull(cb);
		}
	}

	public void testCleanDependentProject() throws CoreException, IOException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();

		cdi2.getNature().getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);

		ResourcesUtils.setBuildAutomatically(true);
		JobUtils.waitForIdle();
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testScopeFromParentProject() throws CoreException, IOException {
		IProducer producer = getProducer("/CDITest2/src/test/Test1.java");

		IScope scope = producer.getScope();
		IAnnotationDeclaration ns = scope.getAnnotationDeclaration(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME);
		IAnnotationDeclaration sd = scope.getAnnotationDeclaration(CDIConstants.SCOPE_ANNOTATION_TYPE_NAME);
		assertNotNull(ns);
		assertNull(sd);

		IFile scope2File = project1.getFile(new Path("src/cdi/test/Scope2.java"));
		IFile scope21File = project1.getFile(new Path("src/cdi/test/Scope2.1"));
		scope2File.setContents(scope21File.getContents(), IFile.FORCE, new NullProgressMonitor());
		JobUtils.waitForIdle();
		
		producer = getProducer("/CDITest2/src/test/Test1.java");
		scope = producer.getScope();
		ns = scope.getAnnotationDeclaration(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME);
		sd = scope.getAnnotationDeclaration(CDIConstants.SCOPE_ANNOTATION_TYPE_NAME);
		assertNull(ns);
		assertNotNull(sd);
	}

	private IProducer getProducer(String file) {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		Set<IBean> beans = cdi2.getBeans(new Path("/CDITest2/src/test/Test1.java"));
		IProducer producer = null;
		for (IBean b: beans) {
			if(b instanceof IProducer) {
				producer = (IProducer)b;
				break;
			}
		}
		assertNotNull(producer);
		return producer;
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project1.delete(true, true, null);
		project2.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

}
