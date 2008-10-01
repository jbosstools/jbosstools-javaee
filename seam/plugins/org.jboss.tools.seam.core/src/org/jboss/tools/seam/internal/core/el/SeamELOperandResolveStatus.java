/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELOperandResolveStatus;
import org.jboss.tools.seam.core.ISeamContextVariable;

public class SeamELOperandResolveStatus extends ELOperandResolveStatus {
	public List<ISeamContextVariable> usedVariables;

	public SeamELOperandResolveStatus(ELInvocationExpression tokens) {
		super(tokens);
	}

	/**
	 * @return List of Seam Context Variables used in EL.  
	 */
	public List<ISeamContextVariable> getUsedVariables() {
		return (usedVariables == null ? new ArrayList<ISeamContextVariable>() : usedVariables);
	}

	/**
	 * @param usedVariables List of Seam Context Variables used in EL.
	 */
	public void setUsedVariables(List<ISeamContextVariable> usedVariables) {
		this.usedVariables = usedVariables;
	}
}
