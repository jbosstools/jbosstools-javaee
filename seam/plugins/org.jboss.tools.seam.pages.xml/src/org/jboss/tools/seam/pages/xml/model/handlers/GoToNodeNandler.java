package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.ArrayList;
import java.util.Properties;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.jst.web.model.handlers.FindItemOnDiagramHandler;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class GoToNodeNandler extends FindItemOnDiagramHandler {

	public GoToNodeNandler() {
	}

	public boolean isEnabled(XModelObject object) {
		if(!super.isEnabled(object)) return false;
		if(!(object instanceof ReferenceObject)) return false;
		if(((ReferenceObject)object).getReference() != null) return false;
		return true;
	}

    protected void fillProperties(XModelObject object, Properties p) {
    	super.fillProperties(object, p);
    	p.put("object", object.getParent());
    	
    	//TODO find all matching nodes taking into account EL.
		XModelObject[] outputs = object.getChildren();
		ArrayList<XModelObject> targets = new ArrayList<XModelObject>();
		for (int i = 0; i < outputs.length; i++) {
			XModelObject o = SeamPagesDiagramStructureHelper.getInstance().getItemOutputTarget(outputs[i]);
			if(o != null) targets.add(o);
		}
		p.put("items", targets.toArray(new XModelObject[0]));
    }

}
