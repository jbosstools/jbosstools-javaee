/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.text.ext.test;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeansDialog;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AssignableBeansDialogTest extends TCKTest {

	public AssignableBeansDialogTest() {}

	public void testAssignableBeansDialog() {
		String path = "JavaSource/org/jboss/jsr299/tck/tests/jbt/lookup/ObjectInjection.java";
		IFile file = tckProject.getFile(path);
		assertTrue(file.exists());
		IInjectionPointField injectionPoint = getInjectionPointField(path, "object");
		
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		AssignableBeansDialog dialog = new AssignableBeansDialog(display.getActiveShell());
		dialog.setBlockOnOpen(false);
		dialog.setInjectionPoint(injectionPoint);
		dialog.open();
		
		List<IBean> bs = dialog.getDisplayedBeans();
		int allBeans = bs.size();
		for (int i = 0; i < dialog.getOptions().size(); i++) {
			dialog.setFilterEnabled(i, true);
		}
		for (int i = 0; i < dialog.getOptions().size(); i++) {
			boolean b = dialog.isFilterEnabled(i);
			dialog.setFilterEnabled(i, !b);
			bs = dialog.getDisplayedBeans();
			assertTrue(bs.size() < allBeans);
			dialog.setFilterEnabled(i, b);
		}
	}
}
