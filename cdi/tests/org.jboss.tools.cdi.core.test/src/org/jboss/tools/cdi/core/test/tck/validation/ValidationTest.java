/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.util.List;

import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.jst.web.kb.internal.validation.ValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidator;

/**
 * @author Alexey Kazakov
 */
public class ValidationTest extends TCKTest {

	protected CDICoreValidator getCDIValidator() {
		ValidationContext context = new ValidationContext(tckProject);
		List<IValidator> validators = context.getValidators();
		for (IValidator validator : validators) {
			if(validator instanceof CDICoreValidator) {
				return (CDICoreValidator)validator;
			}
		}
		return null;
	}
}