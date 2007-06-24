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
package org.jboss.tools.jsf.model.handlers.bean;

import java.util.*;
import org.jboss.tools.common.java.generation.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintQClassName;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class AddManagedBeanSupport extends SpecialWizardSupport {
	XAttributeConstraintQClassName constraint = new XAttributeConstraintQClassName();
	ClassExistsCheck classCheck = new ClassExistsCheck();
	JavaBeanGenerator generator = new JavaBeanGenerator();
	AddManagedBeanPropertiesContext propertiesContext = new AddManagedBeanPropertiesContext();
	boolean isLight = false;
	XEntityData lightData = XEntityDataImpl.create(new String[][]{
		{"AddJSFManagedBeanWizard", "yes"},
		{"managed-bean-scope", "no"},
		{"managed-bean-class", "yes"},
		{"managed-bean-name", "yes"}
	});
	
	public void reset() {
		if(isLight) {
			XEntityData d = getEntityData()[0];
			getEntityData()[0] = lightData;
			XAttributeData[] ad = d.getAttributeData();
			for (int i = 0; i < ad.length; i++) {
				String n = ad[i].getAttribute().getName();
				lightData.setValue(n, d.getValue(n));
			}
		}
		classCheck.setModelContext(getTarget());
		classCheck.update("");
		getProperties().put("propertiesContext", propertiesContext);
		propertiesContext.setType(null);
		generator.setContext(getTarget());
		if(isLight) setAttributeValue(0, "generate source code", "false");
	}

	public void action(String name) throws Exception {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		} else if(NEXT.equals(name)) {
			extractStepData(0);
			propertiesContext.setType(classCheck.getExistingClass());
			setStepId(1);
		} else if(BACK.equals(name)) {
			setStepId(0);
		} else if(HELP.equals(name)) {
			help();
		}
	}
	
	public String[] getActionNames(int stepId) {
		if(stepId == 0) {
			return new String[]{NEXT, FINISH, CANCEL, HELP};
		}
		return new String[]{BACK, FINISH, CANCEL, HELP};
	}
	
	void execute() throws Exception {
		Properties p = extractStepData(0);
		String entity = action.getProperty("entity");
		XModelObject c = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);
		if(getStepId() == 1) {
			propertiesContext.addProperties(c);
		}
		if(isGenerationOn()) generate();
		DefaultCreateHandler.addCreatedObject(getTarget(), c, getProperties());
	}
	
	boolean isGenerationOn() throws Exception {
		Properties p = extractStepData(0);
		if(!"true".equals(p.getProperty("generate source code"))) return false;
		if(!isFieldEditorEnabled(0, "generate source code", p)) return false;
		return true;
	}

	public boolean isActionEnabled(String name) {
		if(NEXT.equals(name)) {
			if(isPropertiesContextEmpty()) return false; 
		} else if(FINISH.equals(name)) {
			//To make customer to check attributes uncomment second condition
			//if(getStepId() == 0 && !isPropertiesContextEmpty()) return false;
		}
		return true;
	}
	
	private boolean isPropertiesContextEmpty() {
		if(!classCheck.classExists()) return true;
		propertiesContext.setType(classCheck.getExistingClass());
		return propertiesContext.size() == 0;
	}
    
	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		String cn = values.getProperty("managed-bean-class");
		classCheck.update(cn);
		if("generate source code".equals(name)) {
			return !isLight && classCheck.isValid() && !classCheck.classExists();
		}
		return true;
	}
	
	void generate() throws Exception {
		Properties input = getGenerateProperties();
		generator.setInput(input);
		generator.generate();
	}
	
	Properties getGenerateProperties() {
		Properties input = new Properties();
		input.put(JavaBeanGenerator.ATT_CLASS_NAME, getAttributeValue(0, "managed-bean-class"));
		input.put("access modifier", "public");
		input.put("extends", "");
		input.put("implements", "");
		input.put("interface", "false");
		return input;
	}

	public WizardDataValidator getValidator(int step) {
		if(step == 0) {
			return super.getValidator(step);
		} else {
			emptyValidator.setSupport(this, step);
			return emptyValidator;
		}		
	}
	
	public String getStepImplementingClass(int stepId) {
		if(stepId == 0) {
			return "org.jboss.tools.jsf.ui.wizard.bean.AddManagedBeanScreenOne";
		} else {
			return "org.jboss.tools.jsf.ui.wizard.bean.AddManagedBeanScreenTwo";
		}
	}

	EmptyValidator emptyValidator = new EmptyValidator();
	
	class EmptyValidator extends DefaultWizardDataValidator {
		public void validate(Properties data) {}
		public String getErrorMessage() {
			return null;
		}
	}
	
	public String getDefaultBeanName(String newClass) {
		if(newClass.length() == 0) return "";
		int dot = newClass.lastIndexOf('.');
		String n = newClass.substring(dot + 1);
		if(n.length() == 0 || getTarget().getChildByPath(n) != null) return null;
		n = n.substring(0, 1).toLowerCase() + n.substring(1);
		classCheck.update(newClass);
		if(!classCheck.classExists()) return null;
		return n;
	}
    
}
