package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class DeleteOutputHandler extends DefaultRemoveHandler {

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		XModelObject redirect = SeamPagesDiagramStructureHelper.instance.getReference(object);
		if(redirect == null) return;
		XModelObject rule = redirect.getParent();
		XModelObject g = rule.getParent();
		XModelObject group = object.getParent();
		super.removeFromParent(rule);
		boolean q = true;
		if(q && g.getChildren().length == 0 && g.getModelEntity().getName().startsWith(SeamPagesConstants.ENT_NAVIGATION)) {
			group.getModel().changeObjectAttribute(group, "persistent", "true");
			super.removeFromParent(g); 
		}		
	}
}
