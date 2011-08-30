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
package org.jboss.tools.cdi.seam.config.ui.marker;

import java.util.Collections;
import java.util.List;

import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.seam.solder.core.generic.IGenericBean;
import org.jboss.tools.cdi.ui.marker.ICDIMarkerResolutionGeneratorExtension;

public class CDIMarkerResolutionGeneratorExtension implements
		ICDIMarkerResolutionGeneratorExtension {

	@Override
	public boolean shouldBeExtended(int id, IBean bean) {
		return (id == CDIValidationErrorManager.AMBIGUOUS_INJECTION_POINTS_ID || id == CDIValidationErrorManager.UNSATISFIED_INJECTION_POINTS_ID) && 
				bean instanceof IGenericBean;
	}

	@Override
	public List<IMarkerResolution> getResolutions(int id, IBean bean) {
		return Collections.emptyList();
	}

}
