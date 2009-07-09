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
package org.jboss.tools.struts.validator.ui.wizard.depends;

import java.util.*;
import org.jboss.tools.struts.validator.ui.wizard.key.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.*;

public class ValidationPathView extends AbstractResourcePathView {
	static Vector<String> history = null;

	protected String[] getRequiredValues() {
		return new String[0];
	}
	
	protected Vector<String> history() {
		if(history == null) {
			history = new Vector<String>();
			XModelObject fs = model.getByPath("FileSystems/WEB-INF");
			if(fs != null) {
				XModelObject[] os = fs.getChildren("FileValidationRules");
				for (int i = 0; i < os.length; i++) history.add(XModelObjectLoaderUtil.getResourcePath(os[i]));
				os = fs.getChildren("FileValidationRules11");
				for (int i = 0; i < os.length; i++) history.add(XModelObjectLoaderUtil.getResourcePath(os[i]));
				String DEFAULT = "/validator-rules.xml";
				if(history.removeElement(DEFAULT)) history.insertElementAt(DEFAULT, 0);
			}
		}
		return history;
	}

	protected String getDisplayName() {
		return "File";
	}

	public void action(String name) {
		XModelObject f = getSelectedFile();
		XModelObject o = model.createModelObject("ValidatorFileSelector", null); //$NON-NLS-1$
		if(f != null)
		  o.setAttributeValue("file", "" + XModelObjectLoaderUtil.getResourcePath(f)); //$NON-NLS-1$ //$NON-NLS-2$
		XActionInvoker.invoke("Edit", o, null); //$NON-NLS-1$
		String sp = o.getAttributeValue("file"); //$NON-NLS-1$
		XModelObject fn = model.getByPath(sp);
		if(fn == null || f == fn) return;
		addValue(sp);
	}

}
