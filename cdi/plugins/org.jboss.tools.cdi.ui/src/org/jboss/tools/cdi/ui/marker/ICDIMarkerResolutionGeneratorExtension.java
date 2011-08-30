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
package org.jboss.tools.cdi.ui.marker;

import java.util.List;

import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.cdi.core.IBean;


public interface ICDIMarkerResolutionGeneratorExtension{
	public boolean shouldBeExtended(int id, IBean bean);
	public List<IMarkerResolution> getResolutions(int id, IBean bean);
}
