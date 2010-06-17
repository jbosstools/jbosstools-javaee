package org.jboss.tools.seam.core.test.validation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.jboss.tools.seam.internal.core.validation.SeamProjectPropertyValidator;

public class SeamProjectProbertyValidatorWrapper extends SeamProjectPropertyValidator implements IValidatorSupport{
	ValidatorSupport support;
	private IProject project;
	
	public SeamProjectProbertyValidatorWrapper(IProject project) {
		this.project = project;
	}

	public void validate() throws ValidationException {
		support.validate();
	}

	public void add(IMarker marker) {
		support.add(marker);
	}

	public boolean isMessageCreated(String template, Object[] parameters) {
		return support.isMessageCreated(template, parameters);
	}

	public void addFile(IFile o) {
		support.addFile(o);
	}

	public List<IMarker> getMarkers() {
		return support.getMarkers();
	}

	public void addMessage(IValidator origin, IMessage message) {
		support.addMessage(origin, message);
	}

	public void validate(IFile file) throws ValidationException {
		support.validate(file);
	}

	public boolean isMessageCreatedOnLine(String markerTemplate,
			Object[] parameters, int lineNumber) throws CoreException {
		return support.isMessageCreatedOnLine(markerTemplate, parameters,
				lineNumber);
	}
}
