package org.jboss.tools.cdi.xml.beans.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.action.impl.DefaultWizardDataValidator;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.WizardDataValidator;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class AddIncludeSupport extends SpecialWizardSupport {

    @Override
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
	
	protected void execute() throws XModelException {
		Properties p0 = extractStepData(0);
		boolean include = "include".equals(p0.getProperty("kind"));
		String entity = getObjectEntity(include);
		
		XModelObject object = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, null);
	
		boolean isRegEx = "true".equals(p0.getProperty("is regular expression"));
		String nameValue = p0.getProperty("name/pattern");
		String nameAttr = isRegEx ? "pattern" : "name";
		object.setAttributeValue(nameAttr, nameValue);

		DefaultCreateHandler.addCreatedObject(getTarget(), object, FindObjectHelper.EVERY_WHERE);
	}

	String getObjectEntity(boolean include) {
		XChild[] cs = getTarget().getModelEntity().getChildren();
		String search = include ? "Include" : "Exclude";
		for (XChild c: cs) {
			String name = c.getName();
			if(name.indexOf(search) >= 0) {
				return name;
			}
		}
		return null;
	}

	protected DefaultWizardDataValidator validator = new Validator();
    
	public WizardDataValidator getValidator(int step) {
		validator.setSupport(this, step);
		return validator;    	
	}

	class Validator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			boolean isRegEx = "true".equals(data.getProperty("is regular expression"));
			String nameValue = data.getProperty("name/pattern");
			String nameAttr = isRegEx ? "pattern" : "name";
			data.setProperty(nameAttr, nameValue);
			super.validate(data);			
		}
		
	}
}
