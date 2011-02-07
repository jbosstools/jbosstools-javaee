package org.jboss.tools.jsf.jsp.ca.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jst.j2ee.internal.common.classpath.J2EEComponentClasspathUpdater;
import org.eclipse.pde.internal.ui.editor.contentassist.TypeContentProposal;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.ui.attribute.adapter.JavaClassContentAssistProvider;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.TestCase;

public class JavaClassContentAssistProviderTest extends TestCase {
	static String jsfProjectName = "testJSFProject";
	static IProject jsfProject;

	public void setUp() throws Exception {
		loadProjects();
		List<IProject> projectList = new ArrayList<IProject>();
		projectList.add(jsfProject);
		J2EEComponentClasspathUpdater.getInstance().forceUpdate(projectList);
		loadProjects();
	}

	private void loadProjects() throws Exception {
		jsfProject = ProjectImportTestSetup.loadProject(jsfProjectName);
		jsfProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public void testJavaClassContentAssistProvider() {
		IModelNature n = EclipseResourceUtil.getModelNature(jsfProject);
		XModel model = n.getModel();
		XModelObject listener = model.createModelObject("WebAppListener24", new Properties());
		assertNotNull(listener);
		XAttribute a = listener.getModelEntity().getAttribute("listener-class");
		JavaClassContentAssistProvider p = new JavaClassContentAssistProvider();
		p.init(listener, null, a);
		IContentProposalProvider pv = p.getContentProposalProvider();
		IContentProposal[] ps = pv.getProposals("java.lang.", 10);
		assertNotNull(ps);
		assertTrue(ps.length > 0);
		assertTrue(ps[0] instanceof TypeContentProposal);
		Set<String> proposals = new HashSet<String>();
		for (IContentProposal c: ps) {
			proposals.add(c.getLabel());
		}
		assertTrue(proposals.contains("Double - java.lang"));
		
	}

}
