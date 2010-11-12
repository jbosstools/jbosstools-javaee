/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package demo;

import org.jboss.seam.annotations.*;

@Name("myCustomAnnotation")
public @interface CustomAnnotation {

	@Name("inner_JBIDE_4144")
	public static class Inner {
		private String innerName;
		
		public String getInnerName() {
			return innerName;
		}
		
		public void setInnerName(String s) {
			innerName = s;
		}
	}

}