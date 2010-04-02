package org.jboss.tools.jsf.ui.editor.check;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.jsf.ui.action.AddJSFNatureActionDelegate;

public class AddJSFCapabilitiesDelegate extends AddJSFNatureActionDelegate {

	private static AddJSFCapabilitiesDelegate instance = new AddJSFCapabilitiesDelegate();

	private AddJSFCapabilitiesDelegate() {
	}

	public static AddJSFCapabilitiesDelegate getInstance(IProject project) {
		instance.setProject(project);
		return instance;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
