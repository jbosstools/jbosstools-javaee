/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.jboss.tools.batch.internal.core.validation.BatchValidationMessages;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class IdValidationService extends ValidationService {
	static String ID_REGEX = "[a-zA-Z_]([a-zA-Z_0-9-.]*)";
	private Property property;
	private Listener listener;

	@Override
	protected void initValidationService() {
		property = context(Property.class);
		listener = new Listener() {
			@Override
			public void handle( final Event event ) {
				refresh();
			}
		};
		attach(listener);
	}

	@Override
	protected Status compute() {
		if(property instanceof Value<?>) {
			Object c = ((Value<?>)property).content();
			if(c != null) {
				String id = c.toString();
				boolean b = id.matches(ID_REGEX);
				if(!b) {
					return Status.createErrorStatus(BatchValidationMessages.ID_IS_NOT_VALID);
				}
			}
		}
		
		return Status.createOkStatus();
	}

	@Override
	public void dispose() {
		super.dispose();
		if(this.listener != null) {
			detach(this.listener);
		}
	}
}
