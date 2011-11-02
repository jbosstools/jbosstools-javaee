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
package org.jboss.tools.cdi.seam.text.ext.test;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.seam.solder.core.test.SeamSolderTest;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.SolderDefaultBeanFilterContributor;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeanFilters;
import org.jboss.tools.cdi.text.ext.hyperlink.AssignableBeansDialog;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AssignableBeansDialogTest extends SeamSolderTest {

	public AssignableBeansDialogTest() {}

	public void testAssignableBeansDialog() {
		String path = "src/org/jboss/defaultbean/Town.java";
		IFile file = getTestProject().getFile(path);
		assertTrue(file.exists());
		IInjectionPointField injectionPoint = getInjectionPointField(getCDIProject(), path, "huge");
		
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		AssignableBeansDialog dialog = new AssignableBeansDialog(display.getActiveShell());
		dialog.setBlockOnOpen(false);
		dialog.setInjectionPoint(injectionPoint);
		dialog.open();
		
		for (int i = 0; i < dialog.getOptions().size(); i++) {
			dialog.setFilterEnabled(i, true);
		}
		
		boolean b = dialog.isFilterEnabled(SolderDefaultBeanFilterContributor.OPTION_DEFAULT_BEAN);
		assertTrue(b);

		List<IBean> bs = dialog.getDisplayedBeans();
		assertEquals(2, bs.size());

		dialog.setFilterEnabled(SolderDefaultBeanFilterContributor.OPTION_DEFAULT_BEAN, !b);
		bs = dialog.getDisplayedBeans();
		assertEquals(1, bs.size());

		dialog.setFilterEnabled(SolderDefaultBeanFilterContributor.OPTION_DEFAULT_BEAN, b);
		bs = dialog.getDisplayedBeans();
		assertEquals(2, bs.size());
	}
}
