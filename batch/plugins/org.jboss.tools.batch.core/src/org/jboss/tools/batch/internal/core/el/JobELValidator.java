/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.el;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.el.core.model.ELArgumentInvocation;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELPropertyInvocation;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolutionImpl;
import org.jboss.tools.common.el.core.resolver.ELSegmentImpl;
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.text.TextProposal;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobELValidator extends JobPropertiesELCompletionEngine {

	public JobELValidator() {}

	/**
	 * This is a resolver for validating only. Content assist is provided by JobPropertiesELCompletionEngine.
	 */
	@Override
	public List<TextProposal> getProposals(ELContext context, String el, int offset) {
		return null;
	}

	/**
	 * This resolver validates operators and accepts any property.
	 * Validation of properties is implemented in BatchValidator.
	 */
	@Override
	protected void resolveLastSegment(IFile file, ELInvocationExpression expr, 
			List<IVariable> members,
			ELResolutionImpl resolution,
			boolean returnEqualedVariablesOnly) {

		ELSegmentImpl segment = new ELSegmentImpl(expr.getFirstToken());
		if(expr instanceof ELPropertyInvocation) {
			segment.setResolved(false);
		} else if (expr instanceof ELArgumentInvocation) {
			segment.setResolved(!members.isEmpty());
		}

		if(segment.getToken() != null) {
			resolution.addSegment(segment);
		}

		if (resolution.isResolved()){
			resolution.setLastResolvedToken(expr);
		}
	}

}
