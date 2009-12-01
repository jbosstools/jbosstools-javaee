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
package org.jboss.tools.seam.ui.internal.project.facet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.jboss.tools.common.ui.IValidator;

/**
 * Delegate class used during validation process in wizard dialog. It contains 
 * map from property name to IValidator instance. It is triggered by property 
 * change event from any registered property editor
 * 
 * @author eskimo
 *
 */
public class DataModelValidatorDelegate implements IDataModelListener {
	
	/**
	 * Target IDataModel instance
	 */
	protected IDataModel model = null;
	
	/**
	 * WizardPage instance that should be validated
	 */
	protected WizardPage page = null;
	
	/**
	 * Map from property name to IValidator instance
	 */
	protected Map<String,IValidator> mapPropToValidator = new HashMap<String,IValidator>();

	private List<String> validationOrder= new ArrayList<String>();
	
	/**
	 * 
	 * @param model
	 * @param page
	 */
	public DataModelValidatorDelegate(IDataModel model,WizardPage page) {
		this.model = model;	
		this.page = page;
		model.addListener(this);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> validate() {
		Map<String, String> errors = new HashMap<String,String>();
		
		return errors;
	}

	/**
	 * 
	 */
	public void propertyChanged(DataModelEvent event) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				validateUntillError();
			}
		});
	}

	/**
	 * 
	 */
	public void validateUntillError() {
		IStatus message = getFirstValidationError();
		if(message == null) {
			page.setMessage(null);
			page.setErrorMessage(null);
		} else {
			if(message.getSeverity()==IStatus.ERROR) {
				page.setErrorMessage(message.getMessage());			
			} else {
				page.setErrorMessage(null);
				page.setMessage(message.getMessage(), DialogPage.WARNING);
			}
		}
		page.setPageComplete(page.getErrorMessage()==null);
	}

	/**
	 * 
	 * @return
	 */
	public IStatus getFirstValidationError() {
		IStatus firstWarning = null;
		for (String validatorName : validationOrder) {
			Map<String,IStatus> errors = getValidator(validatorName).validate(
					model.getProperty(validatorName),model);
			IStatus message = errors.get(validatorName);	
			if(message!=null) {
				if(message.getSeverity()==IStatus.ERROR) {
					return message;
				} else if(message.getSeverity()==IStatus.WARNING && firstWarning==null) {
					firstWarning = message;
				}
			}
		}
		return firstWarning;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public IValidator getValidator(String name) {
		IValidator validator = mapPropToValidator.get(name);
		return validator==null?SeamValidatorFactory.NO_ERRORS_VALIDATOR:validator;
	}
	
	/**
	 * 
	 * @param name
	 * @param validator
	 */
	public void addValidatorForProperty(String name, IValidator validator) {
		mapPropToValidator.put(name, validator);
		validationOrder.add(name);
	}
}
