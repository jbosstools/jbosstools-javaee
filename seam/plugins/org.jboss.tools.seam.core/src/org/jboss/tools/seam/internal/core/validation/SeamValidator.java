 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamTextSourceReference;

/**
 * Basic seam validator.
 * @author Alexey Kazakov
 */
public abstract class SeamValidator implements IValidatorJob {

	private IValidationContext helper;
	private IReporter reporter;

	public SeamValidator() {
		super();
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		this.helper = helper;
		this.reporter = reporter;
		return OK_STATUS;
	}

	public void cleanup(IReporter reporter) {
		reporter = null;
	}

	public void validate(IValidationContext helper, IReporter reporter)	throws ValidationException {
		validateInJob(helper, reporter);
	}

	protected String getBaseName() {
		return "org.jboss.tools.seam.internal.core.validation.messages";
	}

	protected void addError(String messageId, String[] messageArguments, ISeamTextSourceReference target, String messageGroup) {
		IMessage message = new Message(getBaseName(), IMessage.HIGH_SEVERITY, messageId, messageArguments, target.getResource(), messageGroup);
		message.setLength(target.getLength());
		message.setOffset(target.getStartPosition());
		reporter.addMessage(this, message);
	}

	protected void addError(String messageId, ISeamTextSourceReference target, String messageGroup) {
		addError(messageId, new String[0], target, messageGroup);
	}

	protected void removeMessagesFromResources(Set<IResource> resources, String messageGroup) {
		for (IResource r : resources) {
			reporter.removeMessageSubset(this, r, messageGroup);
		}
	}
}