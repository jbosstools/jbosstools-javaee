package org.jboss.tools.seam.core.test.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IValidator;

public class ValidatorSupport implements IReporter, IValidatorSupport {

	List<IMarker> markers = new ArrayList<IMarker>();
	Set<IFile> files = new HashSet<IFile>();
	private IProject project;
	IValidator validator;
	
	public ValidatorSupport(IProject project, IValidator validator) {
		this.project = project;
		this.validator = validator;
	}
	

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.validation.IValidatorSupport#validate()
	 */
	public void validate() throws ValidationException {
		ContextValidationHelper helper = new ContextValidationHelper();
		helper.setProject(this.project);
		validator.validate(files, project, helper, new ValidatorManager(), this);	
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.validation.IValidatorSupport#add(java.lang.String)
	 */
	public void add(IMarker marker) {
		if(marker!=null) {
			markers.add(marker);
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.validation.IValidatorSupport#isMessageCreated(java.lang.String, java.lang.Object[])
	 */
	public boolean isMessageCreated(String template, Object[] parameters) {
		String messagePattern = MessageFormat.format(template,parameters);
		boolean result = false;
		for (IMarker marker : markers) {
			String message = marker.getAttribute(IMarker.MESSAGE,"").toString();
			if(messagePattern.equals(message)){
				result = true;
				break;
			}else if(messagePattern.matches(message)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.validation.IValidatorSupport#addFile(org.eclipse.core.resources.IFile)
	 */
	public void addFile(IFile o) {
		files.add(o);
	}
	
	// IRepoter File
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.validation.IValidatorSupport#getMessages()
	 */
	public List<IMarker> getMarkers() {
		return markers;
	}
	
	public boolean isCancelled() {
		return false;
	}
	
	public void addMessage(
			org.eclipse.wst.validation.internal.provisional.core.IValidator origin,
			IMessage message) {
	}

	public void displaySubtask(
			org.eclipse.wst.validation.internal.provisional.core.IValidator validator,
			IMessage message) {
	}

	public void removeAllMessages(
			org.eclipse.wst.validation.internal.provisional.core.IValidator origin) {
	}

	public void removeAllMessages(
			org.eclipse.wst.validation.internal.provisional.core.IValidator origin,
			Object object) {
	}

	public void removeMessageSubset(
			org.eclipse.wst.validation.internal.provisional.core.IValidator validator,
			Object obj, String groupName) {
	}


	public List getMessages() {
		return null;
	}


	public void validate(IFile file) throws ValidationException {
		addFile(file);
		validate();
	}


	public boolean isMessageCreatedOnLine(String markerTemplate,
			Object[] parameters, int lineNumber) throws CoreException {
		String messagePattern = MessageFormat.format(markerTemplate,parameters);
		boolean result = false;
		for (IMarker marker : markers) {
			String message = marker.getAttribute(IMarker.MESSAGE,"").toString();
			if(messagePattern.matches(message)) {
				Object line = marker.getAttribute(IMarker.LINE_NUMBER);
				if(line!=null && line instanceof Integer && ((Integer)line).intValue()==lineNumber) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

}
