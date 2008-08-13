package org.jboss.tools.seam.pages.xml.model.helpers.autolayout;

import org.jboss.tools.jst.web.model.helpers.autolayout.Items;
import org.jboss.tools.jst.web.model.helpers.autolayout.LayuotConstants;

public class SeamPagesItems extends Items {

	public boolean isZigzagging() {
		return false;
	}

    protected LayuotConstants createConstants() {
    	return new SeamLayoutConstants();
    }

}

class SeamLayoutConstants extends LayuotConstants {

	public void update() {
		deltaX = 320;
	}

}
