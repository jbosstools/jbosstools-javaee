package org.jboss.tools.seam.pages.xml.model.helpers.autolayout;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.helpers.autolayout.Items;
import org.jboss.tools.jst.web.model.helpers.autolayout.LayuotConstants;
import org.jboss.tools.jst.web.model.helpers.autolayout.TransitionArranger;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class SeamPagesItems extends Items {

	public boolean isZigzagging() {
		return false;
	}

    protected LayuotConstants createConstants() {
    	return new SeamLayoutConstants();
    }

    protected TransitionArranger createTransitionArranger() {
    	return new SeamPagesTransitionArranger();
    }
}

class SeamLayoutConstants extends LayuotConstants {

	public void update() {
		deltaX = 320;
	}

}

class SeamPagesTransitionArranger extends TransitionArranger {
	public void execute() {
		super.execute();

		for (int i = 0; i < items.length; i++) {
			XModelObject[] links = items[i].getObject().getChildren();
			if(links.length < 2) continue;
			Set<String> paths = new HashSet<String>();
			for (int j = 0; j < links.length; j++) {
				String path = links[j].getAttributeValue(SeamPagesConstants.ATTR_PATH);
				if(paths.contains(path)) {
					if(!SeamPagesDiagramStructureHelper.getInstance().isShortcut(links[j])) {
						try {
							links[j].getModel().changeObjectAttribute(links[j], "shortcut", "yes");
						} catch (XModelException e) {
							e.printStackTrace();
						}
						System.out.println("Set shortcut " + path);
					}
				} else {
					paths.add(path);
				}
			}
		}
	}

}
