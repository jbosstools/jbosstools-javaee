/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.ui.dialog;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author snjeza
 */
public class RemovedFileSetProvider extends AbstractFileSetContentProvider {

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof File) {
			File dir = (File) newInput;
			elements = dir.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					if (pathname.getName().endsWith(".jar") || pathname.getName().endsWith(".zip")) { //$NON-NLS-1$ //$NON-NLS-2$
						return true;
					}
					return false;
				}
				
			});
		} else {
			elements = ZERO_ARRAY;
		}
		sort();
	}

}
