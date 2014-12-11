/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.services;

import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.OutcomeElement;
import org.jboss.tools.batch.ui.editor.internal.model.Split;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */

public final class NextPossibleValuesService extends PossibleValuesService {
	
	private Listener listener;

	@Override
	protected void initPossibleValuesService() {
		this.invalidValueSeverity = Status.Severity.OK;
		listener = new Listener() {
            @Override
            public void handle( final Event event ) {
            	if(listener == null) {
            		//disposed
            		return;
            	}
           		refresh();
            }
        };
		
	}

	@Override
	protected void compute(final Set<String> values) {
		Value<?> value = context(Value.class);
		if (value != null && value.element() != null && value.element().parent() != null) {
			Element element = value.element().parent().element();
			if(value.element() instanceof OutcomeElement) {
				element = element.parent().element();
			}
			if(element instanceof Job) {
				reattachListener(element, "FlowElements");
				reattachListener(element, "FlowElements/Id");
				for (FlowElement f: ((Job)element).getFlowElements()) {
					if(f.getId().content() != null) {
						values.add(f.getId().content());
					}
				}
			} else if(element instanceof Flow) {
				reattachListener(element, "FlowElements");
				reattachListener(element, "FlowElements/Id");
				for (FlowElement f: ((Flow)element).getFlowElements()) {
					if(f.getId().content() != null) {
						values.add(f.getId().content());
					}
				}
			} else if(element instanceof Split) {
				reattachListener(element, "Flows");
				reattachListener(element, "Flows/Id");
				for (Flow f: ((Split)element).getFlows()) {
					if(f.getId().content() != null) {
						values.add(f.getId().content());
					}
				}
				
			}
		}
	}

	private void reattachListener(Element element, String path) {
		element.detach(listener, path);
		element.attach(listener, path);
	}

	@Override
	public void dispose() {
		super.dispose();
		listener = null;
	}

}
