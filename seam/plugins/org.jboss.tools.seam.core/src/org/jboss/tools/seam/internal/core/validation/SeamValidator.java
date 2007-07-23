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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.internal.core.SeamProject;

/**
 * Basic seam validator.
 * @author Alexey Kazakov
 */
public abstract class SeamValidator implements IValidatorJob {

	protected static final String MARKED_SEAM_RESOURCE_MESSAGE_GROUP = "markedSeamCoreResource";

	protected static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";
	protected static final String UNKNOWN_VARIABLE_NAME_MESSAGE_ID = "UNKNOWN_VARIABLE_NAME";
	protected static final String STATEFUL_COMPONENT_DOES_NOT_CONTAIN_METHOD_SUFIX_MESSAGE_ID = "STATEFUL_COMPONENT_DOES_NOT_CONTAIN_";
	protected static final String DUPLICATE_METHOD_PREFIX_MESSAGE_ID = "DUPLICATE_";
	protected static final String REMOVE_METHOD_SUFIX_MESSAGE_ID = "REMOVE";
	protected static final String DESTROY_METHOD_SUFIX_MESSAGE_ID = "DESTROY";
	protected static final String CREATE_METHOD_SUFIX_MESSAGE_ID = "CREATE";
	protected static final String UNWRAP_METHOD_SUFIX_MESSAGE_ID = "UNWRAP";
	protected static final String OBSERVER_METHOD_SUFIX_MESSAGE_ID = "OBSERVER";
	protected static final String NONCOMPONENTS_METHOD_SUFIX_MESSAGE_ID = "_DOESNT_BELONG_TO_COMPONENT";
	protected static final String STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "STATEFUL_COMPONENT_WRONG_SCOPE";
	protected static final String ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "ENTITY_COMPONENT_WRONG_SCOPE";
	protected static final String UNKNOWN_FACTORY_NAME_MESSAGE_ID = "UNKNOWN_FACTORY_NAME";
	protected static final String MULTIPLE_DATA_BINDER_MESSAGE_ID = "MULTIPLE_DATA_BINDER";
	protected static final String DUPLICATE_VARIABLE_NAME_MESSAGE_ID = "DUPLICATE_VARIABLE_NAME";
	protected static final String UNKNOWN_DATA_MODEL_MESSAGE_ID = "UNKNOWN_DATA_MODEL";
	protected static final String UNKNOWN_COMPONENT_CLASS_NAME_MESSAGE_ID = "UNKNOWN_COMPONENT_CLASS_NAME";
	protected static final String UNKNOWN_COMPONENT_PROPERTY_MESSAGE_ID = "UNKNOWN_COMPONENT_PROPERTY";
	protected static final String INVALID_EXPRESSION_MESSAGE_ID = "INVALID_EXPRESSION";
	protected static final String UNPAIRED_GETTER_OR_SETTER_MESSAGE_ID = "UNPAIRED_GETTER_OR_SETTER";

	protected SeamValidationHelper coreHelper;
	protected IReporter reporter;
	protected SeamValidationContext validationContext;

	protected ISeamProject project;

	public SeamValidator() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		this.coreHelper = (SeamValidationHelper)helper;
		this.reporter = reporter;
		this.project = coreHelper.getSeamProject();
		IStatus status = null;
		try {
			this.validationContext = ((SeamProject)project).getValidationContext();
			Set<IFile> changedFiles = coreHelper.getChangedFiles();
			if(changedFiles.size()>0) {
				status = validate(changedFiles);
			} else {
				status = validateAll();
			}
		} finally {
			validationContext.getRemovedFiles().clear();
			validationContext.getRegisteredFiles().clear();
		}
		return status;
	}

	/**
	 * Incremental Validation
	 * @return
	 * @throws ValidationException
	 */
	abstract public IStatus validate(Set<IFile> changedFiles) throws ValidationException;

	/**
	 * Full Validation
	 * @return
	 * @throws ValidationException
	 */
	abstract public IStatus validateAll() throws ValidationException;

	public void cleanup(IReporter reporter) {
		reporter = null;
	}

	public void validate(IValidationContext helper, IReporter reporter)	throws ValidationException {
		validateInJob(helper, reporter);
	}

	protected String getBaseName() {
		return "org.jboss.tools.seam.internal.core.validation.messages";
	}

	protected void addError(String messageId, String preferenceKey, String[] messageArguments, ISeamTextSourceReference location, IResource target, String messageGroup) {
		addError(messageId, preferenceKey, messageArguments, location.getLength(), location.getStartPosition(), target, messageGroup);
	}

	protected void addError(String messageId, String preferenceKey, ISeamTextSourceReference location, IResource target, String messageGroup) {
		addError(messageId, preferenceKey, new String[0], location, target, messageGroup);
	}

	protected void addError(String messageId, String preferenceKey, String[] messageArguments, int length, int offset, IResource target, String messageGroup) {
		String preferenceValue = SeamPreferences.getProjectPreference(project, preferenceKey);
		boolean ignore = false;
		int messageSeverity = IMessage.HIGH_SEVERITY;
		if(SeamPreferences.WARNING.equals(preferenceValue)) {
			messageSeverity = IMessage.NORMAL_SEVERITY;
		} else if(SeamPreferences.IGNORE.equals(preferenceValue)) {
			ignore = true;
		}

		IMessage message = new Message(getBaseName(), messageSeverity, messageId, messageArguments, target, messageGroup);
		message.setLength(length);
		message.setOffset(offset);
		if(!ignore) {
			reporter.addMessage(this, message);
		}
	}

	protected void removeMessagesFromResources(Set<IResource> resources, String messageGroup) {
		for (IResource r : resources) {
			reporter.removeMessageSubset(this, r, messageGroup);
		}
	}
}