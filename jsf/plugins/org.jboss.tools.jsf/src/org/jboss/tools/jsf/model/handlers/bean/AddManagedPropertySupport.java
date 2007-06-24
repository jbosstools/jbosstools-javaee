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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.common.java.generation.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintJavaName;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.model.handlers.ChangeContentKindHandler;
import org.jboss.tools.jsf.model.helpers.bean.BeanHelper;

public class AddManagedPropertySupport extends SpecialWizardSupport {
	ClassExistsCheck classCheck = new ClassExistsCheck();
	JavaPropertyGenerator generator = new JavaPropertyGenerator();
	XAttributeConstraintJavaName constraint = new XAttributeConstraintJavaName();	
	Map<String,IJavaElement> fields;
	boolean isLight = false;
	XEntityData lightData = XEntityDataImpl.create(new String[][]{
		{"AddJSFManagedPropertyWizard", "yes"},
		{"property-name", "yes"},
		{"property-class", "no"},
		{"value-kind", "no"},
		{"value", "no"}
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
		if(ChangeContentKindHandler.isNewValueKind(getTarget(), "properties") &&
		   !ChangeContentKindHandler.checkChangeSignificance(getTarget())) {
		   	setFinished(true);
		   	return;
		}
		classCheck.setModelContext(getTarget());
		String s = getTarget().getAttributeValue("managed-bean-class");
		if(s == null) s = "";
		classCheck.update(s);
		try {
			fields = BeanHelper.getJavaProperties(classCheck.getExistingClass());
		} catch (Exception e) {
			fields = new TreeMap<String,IJavaElement>();
		}
		XModelObject[] cs = getTarget().getChildren("JSFManagedProperty");
		for (int i = 0; i < cs.length; i++) fields.remove(cs[i].getAttributeValue("property-name"));
		String[] fs = (String[])fields.keySet().toArray(new String[0]);
		setValueList(0, "property-name", fs);
		if(isLight) setAttributeValue(0, "add java property", "false");
	}

	public void action(String name) throws Exception {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		} else if(HELP.equals(name)) {
			help();
		}
	}
	
	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}
	
	void execute() throws Exception {
		Properties p = extractStepData(0);
		getTarget().setAttributeValue("content-kind", "properties");
		String entity = action.getProperty("entity");
		XModelObject c = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);
		DefaultCreateHandler.addCreatedObject(getTarget(), c, getProperties());
		getProperties().put("created", c);
		if(!isGenerationOn(p)) return;
		generate(p);
	}

	boolean isGenerationOn(Properties p) {
		if(!"true".equals(p.getProperty("add java property"))) return false;
		if(!isFieldEditorEnabled(0, "add java property", p)) return false;
		return true;
	}	

	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		String pn = values.getProperty("property-name");
		String vk = values.getProperty("value-kind");
		boolean canGenerate = classCheck.isValid() && classCheck.classExists() && !classCheck.getExistingClass().isBinary();
		boolean canGenerateField = !fields.containsKey(pn);
		if("add java property".equals(name)) {
			return !isLight && canGenerate && canGenerateField;
		}
		if("generate getter".equals(name) || "generate setter".equals(name)) {
			boolean agp = "true".equals(values.getProperty("add java property"));
			return agp && canGenerate && canGenerateField;
		} else if("value".equals(name)) {
			return "value".equals(vk);
		}
		return true;
	}
	
	public String getFieldType(String name) {
		IMember m = (IMember)fields.get(name);
		return (m == null) ? null : EclipseJavaUtil.getMemberTypeAsString(m);
	}
	
	void generate(Properties p) throws Exception {
		generator.setOwner(classCheck.getExistingClass());
		String type = p.getProperty("property-class");
		if(type.length() == 0) type = "String";
		String name = p.getProperty("property-name");
		boolean getter = "true".equals(p.getProperty("generate getter")); 
		boolean setter = "true".equals(p.getProperty("generate setter")); 
		generator.generate(name, type, "public", true, getter, setter);		
	}

	public String getStepImplementingClass(int stepId) {
		return "org.jboss.tools.jsf.ui.wizard.bean.AddManagedBeanPropertyScreen";
	}
	
	protected DefaultWizardDataValidator validator = new PropertyValidator();
    
	public WizardDataValidator getValidator(int step) {
		validator.setSupport(this, step);
		return validator;    	
	}
	
	class PropertyValidator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			String propertyName = data.getProperty("property-name");
			message = DefaultCreateHandler.getConstraintMessage("property-name", propertyName, constraint);
		}
	}

}
