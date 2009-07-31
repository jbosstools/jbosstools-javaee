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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.java.generation.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintJavaName;
import org.jboss.tools.common.model.XModelException;
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
		{"AddJSFManagedPropertyWizard", "yes"}, //$NON-NLS-1$ //$NON-NLS-2$
		{"property-name", "yes"}, //$NON-NLS-1$ //$NON-NLS-2$
		{"property-class", "no"}, //$NON-NLS-1$ //$NON-NLS-2$
		{"value-kind", "no"}, //$NON-NLS-1$ //$NON-NLS-2$
		{"value", "no"} //$NON-NLS-1$ //$NON-NLS-2$
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
		if(ChangeContentKindHandler.isNewValueKind(getTarget(), "properties") && //$NON-NLS-1$
		   !ChangeContentKindHandler.checkChangeSignificance(getTarget())) {
		   	setFinished(true);
		   	return;
		}
		classCheck.setModelContext(getTarget());
		String s = getTarget().getAttributeValue("managed-bean-class"); //$NON-NLS-1$
		if(s == null) s = ""; //$NON-NLS-1$
		classCheck.update(s);
		try {
			fields = BeanHelper.getJavaProperties(classCheck.getExistingClass());
		} catch (JavaModelException e) {
			fields = new TreeMap<String,IJavaElement>();
		}
		XModelObject[] cs = getTarget().getChildren("JSFManagedProperty"); //$NON-NLS-1$
		for (int i = 0; i < cs.length; i++) fields.remove(cs[i].getAttributeValue("property-name")); //$NON-NLS-1$
		String[] fs = (String[])fields.keySet().toArray(new String[0]);
		setValueList(0, "property-name", fs); //$NON-NLS-1$
		if(isLight) setAttributeValue(0, "add java property", "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void action(String name) throws XModelException {
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
	
	void execute() throws XModelException {
		Properties p = extractStepData(0);
		getTarget().setAttributeValue("content-kind", "properties"); //$NON-NLS-1$ //$NON-NLS-2$
		String entity = action.getProperty("entity"); //$NON-NLS-1$
		XModelObject c = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);
		DefaultCreateHandler.addCreatedObject(getTarget(), c, getProperties());
		getProperties().put("created", c); //$NON-NLS-1$
		if(!isGenerationOn(p)) return;
		try {
			generate(p);
		} catch (CoreException e) {
			throw new XModelException(e);
		}
	}

	boolean isGenerationOn(Properties p) {
		if(!"true".equals(p.getProperty("add java property"))) return false; //$NON-NLS-1$ //$NON-NLS-2$
		if(!isFieldEditorEnabled(0, "add java property", p)) return false; //$NON-NLS-1$
		return true;
	}	

	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		String pn = values.getProperty("property-name"); //$NON-NLS-1$
		String vk = values.getProperty("value-kind"); //$NON-NLS-1$
		boolean canGenerate = classCheck.isValid() && classCheck.classExists() && !classCheck.getExistingClass().isBinary();
		boolean canGenerateField = !fields.containsKey(pn);
		if("add java property".equals(name)) { //$NON-NLS-1$
			return !isLight && canGenerate && canGenerateField;
		}
		if("generate getter".equals(name) || "generate setter".equals(name)) { //$NON-NLS-1$ //$NON-NLS-2$
			boolean agp = "true".equals(values.getProperty("add java property")); //$NON-NLS-1$ //$NON-NLS-2$
			return agp && canGenerate && canGenerateField;
		} else if("value".equals(name)) { //$NON-NLS-1$
			return "value".equals(vk); //$NON-NLS-1$
		}
		return true;
	}
	
	public String getFieldType(String name) {
		IMember m = (IMember)fields.get(name);
		return (m == null) ? null : EclipseJavaUtil.getMemberTypeAsString(m);
	}
	
	void generate(Properties p) throws CoreException {
		generator.setOwner(classCheck.getExistingClass());
		String type = p.getProperty("property-class"); //$NON-NLS-1$
		if(type.length() == 0) type = "String"; //$NON-NLS-1$
		String name = p.getProperty("property-name"); //$NON-NLS-1$
		boolean getter = "true".equals(p.getProperty("generate getter"));  //$NON-NLS-1$ //$NON-NLS-2$
		boolean setter = "true".equals(p.getProperty("generate setter"));  //$NON-NLS-1$ //$NON-NLS-2$
		generator.generate(name, type, "public", true, getter, setter);		 //$NON-NLS-1$
	}

	public String getStepImplementingClass(int stepId) {
		return "org.jboss.tools.jsf.ui.wizard.bean.AddManagedBeanPropertyScreen"; //$NON-NLS-1$
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
			String propertyName = data.getProperty("property-name"); //$NON-NLS-1$
			message = DefaultCreateHandler.getConstraintMessage("property-name", propertyName, constraint); //$NON-NLS-1$
		}
	}

}
