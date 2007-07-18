/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.internal.project.facet;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author eskimo
 *
 */
public class DataModelValidatorDelegate implements IDataModelListener {
	
	/**
	 * 
	 */
	protected IDataModel model = null;
	
	
	/**
	 * 
	 */
	protected WizardPage page = null;
	
	/**
	 * 
	 */
	protected Map<String,IValidator> mapPropToValidator = new HashMap<String,IValidator>();
	
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
		//When SeamFacetInstallDelegete sets model property,
		//it can result in Invalid thread access.
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				validateUntillError();
			}
		});
	}
	
	/**
	 * 
	 */
	public void validateUntillError() {
		page.setErrorMessage(getFirstValidationError());
		page.setPageComplete(page.getErrorMessage()==null);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFirstValidationError() {
		for (String validatorName : mapPropToValidator.keySet()) {
			Map<String,String> errors = getValidator(validatorName).validate(
					model.getProperty(validatorName),model);
			String message = errors.get(validatorName);	
			if(message!=null) {
				return message;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public IValidator getValidator(String name) {
		IValidator validator = mapPropToValidator.get(name);
		return validator==null?ValidatorFactory.NO_ERRORS_VALIDATOR:validator;
	}
	
	/**
	 * 
	 * @param name
	 * @param validator
	 */
	public void addValidatorForProperty(String name, IValidator validator) {
		mapPropToValidator.put(name, validator);
	}
}
