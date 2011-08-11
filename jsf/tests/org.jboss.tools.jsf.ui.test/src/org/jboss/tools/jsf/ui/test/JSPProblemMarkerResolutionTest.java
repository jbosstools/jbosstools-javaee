package org.jboss.tools.jsf.ui.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.web.ui.action.JSPProblemMarkerResolutionGenerator;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

public class JSPProblemMarkerResolutionTest extends AbstractResourceMarkerTest{
	IProject project = null;

	public JSPProblemMarkerResolutionTest() {
		super("JSP Problem Marker Resolution Tests");
	}

	public JSPProblemMarkerResolutionTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test_jsf_project");
	}

	public void testProblemMarkerResolutionInJSP() throws CoreException {
		IFile jspFile = project.getFile("WebContent/pages/test_page1.jsp");

		assertMarkerIsCreated(jspFile, "org.eclipse.jst.jsp.core.validationMarker", "Unknown tag (h:commandButton).", true, 8);

		IMarker[] markers = findMarkers(jspFile, "org.eclipse.jst.jsp.core.validationMarker", "Unknown tag (h:commandButton).");

		assertEquals(1, markers.length);

		JSPProblemMarkerResolutionGenerator generator = new JSPProblemMarkerResolutionGenerator();

		for(IMarker marker : markers){
			generator.hasResolutions(marker);
			IMarkerResolution[] resolutions = generator.getResolutions(marker);
			for(IMarkerResolution resolution : resolutions){
				resolution.run(marker);
			}
		}

		TestUtil.validate(jspFile);

		assertMarkerIsNotCreated(jspFile, "org.eclipse.jst.jsp.core.validationMarker", "Unknown tag (h:commandButton).");
	}

	public void testProblemMarkerResolutionInXHTML() throws CoreException {
		IFile jspFile = project.getFile("WebContent/pages/test_page2.xhtml");

		assertMarkerIsCreated(jspFile, "org.eclipse.wst.html.core.validationMarker", "Unknown tag (ui:insert).", true, 8, 17, 31);

		IMarker[] markers = findMarkers(jspFile, "org.eclipse.wst.html.core.validationMarker", "Unknown tag (ui:insert).");

		assertEquals(3, markers.length);

		JSPProblemMarkerResolutionGenerator generator = new JSPProblemMarkerResolutionGenerator();

		for(IMarker marker : markers){
			generator.hasResolutions(marker);
			IMarkerResolution[] resolutions = generator.getResolutions(marker);
			for(IMarkerResolution resolution : resolutions){
				resolution.run(marker);
			}
		}

		TestUtil.validate(jspFile);

		assertMarkerIsNotCreated(jspFile, "org.eclipse.wst.html.core.validationMarker", "Unknown tag (ui:insert).");
	}
}