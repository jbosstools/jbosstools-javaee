package org.jboss.tools.seam.core.test.validation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.seam.internal.core.validation.SeamCoreValidator;

public class SeamCoreValidatorWrapper extends SeamCoreValidator implements IValidatorSupport{

	ValidatorSupport validatorSupport;
	
	public SeamCoreValidatorWrapper(IProject project) {
		this.validatorSupport = new ValidatorSupport(project,this);
	}

	@Override
	public IMarker addError(String message, int severity, Object[] messageArguments, int lineNumber, int length, int offset, IResource target, TextFileDocumentProvider documentProvider, String markerId, Class markerOwner) {
		IMarker marker = super.addError(message, severity, messageArguments, lineNumber, length, offset, target, documentProvider, markerId, markerOwner);
		validatorSupport.add(marker);
		return marker;
	}

	@Override
	public IMarker addError(String message, int severity,
			String[] messageArguments, int lineNumber, int length, int offset,
			IResource target) {
		IMarker marker = super.addError(message, severity, messageArguments, lineNumber, length, offset,
				target);
		validatorSupport.add(marker);
		return marker;
	}
	@Override
	public IMarker addError(String message, String preferenceKey,
			String[] messageArguments, int length, int offset,
			IResource target) {

		IMarker marker = super.addError(message, preferenceKey, messageArguments, length, offset,
				target);
		validatorSupport.add(marker);
		return marker;
	}
	
	@Override
	public IMarker addError(String message, String preferenceKey,
			String[] messageArguments, int lineNumber, int length, int offset,
			IResource target) {

		IMarker marker = super.addError(message, preferenceKey, messageArguments, lineNumber, length, offset,
				target);
		validatorSupport.add(marker);
		return marker;
	}
	
	public boolean isMessageCreated(String template, Object[] parameters) {
		return validatorSupport.isMessageCreated(template,parameters);
	}

	public List<IMarker> getMessages() {
		return validatorSupport.getMarkers();
	}
	
	public void addFile(IFile o) {
		validatorSupport.addFile(o);
	}
	
	public void validate() throws ValidationException {
		validatorSupport.validate();	
	}
	
	public void validate(IFile file) throws ValidationException {
		validatorSupport.addFile(file);
		validatorSupport.validate();	
	}
	public void add(IMarker marker) {
		validatorSupport.add(marker);
	}
	
	public List<IMarker> getMarkers() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isMessageCreatedOnLine(String markerTemplate,
			Object[] parameters, int lineNumber) throws CoreException {
		return validatorSupport.isMessageCreatedOnLine(markerTemplate, parameters, lineNumber);
	}
}
