package org.jboss.tools.seam.core.test.validation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.jsf.web.validation.ELValidator;

public class ELValidatorWrapper extends ELValidator implements IValidatorSupport{
	
	ValidatorSupport validatorSupport;
	
	public ELValidatorWrapper(IProject project) {
		this.validatorSupport = new ValidatorSupport(project,this);
	}
	@Override
	public IMarker addError(String message, int severity,
			String[] messageArguments, int lineNumber, int length, int offset,
			IResource target) {
		
		IMarker marker=  super.addError(message, severity, messageArguments, lineNumber, length, offset,
				target);
		validatorSupport.add(marker);
		return marker;
	}
	
	@Override
	public IMarker addError(String message, String preferenceKey,
			String[] messageArguments, int length, int offset,
			IResource target) {
		IMarker marker =  super.addError(message, preferenceKey, messageArguments, length, offset,
				target);
		validatorSupport.add(marker);
		return marker;
	}
	
	@Override
	public IMarker addError(String message, String preferenceKey,
			String[] messageArguments, int lineNumber, int length, int offset,
			IResource target) {
		IMarker marker =  super.addError(message, preferenceKey, messageArguments, lineNumber, length, offset,
				target);
		validatorSupport.add(marker);
		return marker;
	}
	
	public boolean isMessageCreated(String template, Object[] parameters) {
		return validatorSupport.isMessageCreated(template,parameters);
	}

	public List<IMarker> getMarkers() {
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
	public void add(IMarker message) {
		validatorSupport.add(message);
		
	}
	public boolean isMessageCreatedOnLine(String markerTemplate,
			Object[] parameters, int lineNumber) throws CoreException {
		return validatorSupport.isMessageCreatedOnLine(markerTemplate, parameters, lineNumber);
	}
}
