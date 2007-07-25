 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.SeamPreferences;

/**
 * Abstract implementation of ISeamvalidator
 * @author Alexey Kazakov
 */
public abstract class SeamValidator implements ISeamValidator {

	IStatus OK_STATUS = new Status(IStatus.OK, "org.eclipse.wst.validation", 0, "OK", null); //$NON-NLS-1$ //$NON-NLS-2$

	protected SeamValidatorManager validationManager;
	protected SeamValidationHelper coreHelper;
	protected IReporter reporter;
	protected SeamValidationContext validationContext;
	protected ISeamProject project;

	public SeamValidator(SeamValidatorManager validatorManager, SeamValidationHelper coreHelper, IReporter reporter, SeamValidationContext validationContext, ISeamProject project) {
		this.validationManager = validatorManager;
		this.coreHelper = coreHelper;
		this.project = project;
		this.reporter = reporter;
		this.validationContext = validationContext;
	}

	protected String getBaseName() {
		return "org.jboss.tools.seam.internal.core.validation.messages";
	}

	protected void addError(String messageId, String preferenceKey, String[] messageArguments, ISeamTextSourceReference location, IResource target) {
		addError(messageId, preferenceKey, messageArguments, location.getLength(), location.getStartPosition(), target);
	}

	protected void addError(String messageId, String preferenceKey, ISeamTextSourceReference location, IResource target) {
		addError(messageId, preferenceKey, new String[0], location, target);
	}

	protected void addError(String messageId, String preferenceKey, String[] messageArguments, int length, int offset, IResource target) {
		String preferenceValue = SeamPreferences.getProjectPreference(project, preferenceKey);
		boolean ignore = false;
		int messageSeverity = IMessage.HIGH_SEVERITY;
		if(SeamPreferences.WARNING.equals(preferenceValue)) {
			messageSeverity = IMessage.NORMAL_SEVERITY;
		} else if(SeamPreferences.IGNORE.equals(preferenceValue)) {
			ignore = true;
		}

		IMessage message = new Message(getBaseName(), messageSeverity, messageId, messageArguments, target, ISeamValidator.MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
		message.setLength(length);
		message.setOffset(offset);
		if(!ignore) {
			reporter.addMessage(validationManager, message);
		}
	}

	protected void removeMessagesFromResources(Set<IResource> resources, String messageGroup) {
		for (IResource r : resources) {
			reporter.removeMessageSubset(validationManager, r, messageGroup);
		}
	}
}