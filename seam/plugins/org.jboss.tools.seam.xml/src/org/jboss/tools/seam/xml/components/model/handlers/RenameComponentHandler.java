package org.jboss.tools.seam.xml.components.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.SpecialWizardFactory;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;

public class RenameComponentHandler extends AbstractHandler {

	public RenameComponentHandler() {
	}

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(isEnabled(object)) {
			SpecialWizard wizard = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.seam.ui.views.actions.RenameComponentAction");
			wizard.setObject(object);
			wizard.execute();
		}
	}

}
