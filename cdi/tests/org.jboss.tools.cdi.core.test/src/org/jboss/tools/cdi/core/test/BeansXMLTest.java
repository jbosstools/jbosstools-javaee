package org.jboss.tools.cdi.core.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class BeansXMLTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;

	public BeansXMLTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest1");
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void testBeansXML() throws CoreException, IOException {
		IFile file = project.getFile(new Path("META-INF/beans.xml"));
		assertNotNull(file);
		XModelObject beansXML = EclipseResourceUtil.createObjectForResource(file);
		assertNotNull(beansXML);

		assertEquals("FileCDIBeans", beansXML.getModelEntity().getName());

		XModelObject o = findTag(beansXML, "drools:RuleResources");
		assertNotNull(o);
		
		o = findTag(beansXML, "drools:DroolsConfig/drools:ruleResources/s:Inject");
		assertNotNull(o);
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	XModelObject findTag(XModelObject parent, String path) {
		XModelObject[] cs = parent.getChildren(XModelObjectLoaderUtil.ENT_ANY_ELEMENT);
		for (XModelObject o: cs) {
			String name = o.getAttributeValue("tag");
			if(name == null) continue;
			if(path.equals(name)) return o;
			if(path.startsWith(name + "/")) {
				return findTag(o, path.substring(name.length() + 1));
			}
		}
		return null;
	}

}
