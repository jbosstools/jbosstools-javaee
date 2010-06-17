package org.jboss.tools.seam.core.test.validation;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.core.ValidationException;

public interface IValidatorSupport {

	public abstract void validate() throws ValidationException;
	
	public abstract void validate(IFile file) throws ValidationException;	

	public abstract void add(IMarker marker);

	public abstract boolean isMessageCreated(String template,
			Object[] parameters);

	public abstract void addFile(IFile o);

	public abstract List<IMarker> getMarkers();

	public abstract boolean isMessageCreatedOnLine(String markerTemplate,
			Object[] parameters, int lineNumber) throws CoreException;

}