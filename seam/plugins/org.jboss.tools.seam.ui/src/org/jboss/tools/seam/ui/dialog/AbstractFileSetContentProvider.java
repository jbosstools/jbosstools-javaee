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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.ibm.icu.text.Collator;

/**
 * 
 * @author snjeza
 */
public abstract class AbstractFileSetContentProvider implements
		IStructuredContentProvider {

	protected static File[] ZERO_ARRAY = new File[0];
	protected Object[] elements = ZERO_ARRAY;
	private final Comparator<File> comparer = new Comparator<File>() {
		private Collator collator = Collator.getInstance();

		public int compare(File arg0, File arg1) {
			String s1 = arg0.getName();
			String s2 = arg1.getName();
			return collator.compare(s1, s2);
		}
	};
	public Object[] getElements(Object inputElement) {
		return elements;
	}

	public void dispose() {
		
	}

	public void sort() {
		File[] results = new File[elements.length];
		System.arraycopy(elements, 0, results, 0, elements.length);
		Collections.sort(Arrays.asList(results), comparer);
		elements = results;
	}
}
