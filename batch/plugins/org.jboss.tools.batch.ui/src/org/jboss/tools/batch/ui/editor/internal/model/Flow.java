/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 *     Tomas Milata - Added Batch diagram editor (JBIDE-19717).
 ************************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "flow" )
@Image ( path = "flow.png" )
@XmlBinding( path = "flow" )
public interface Flow extends FlowElement, FlowElementsContainer, NextAttributeElement {
	
	ElementType TYPE = new ElementType( Flow.class );
    
	
	@Type( base = OutcomeElement.class,
			possible = {
				End.class,
				Fail.class,
				Stop.class,
				Next.class
			}
	)
	@XmlListBinding( path = "" )
	ListProperty PROP_OUTCOME_ELEMENTS = new ListProperty(TYPE, "OutcomeElements");

	ElementList<OutcomeElement> getOutcomeElements();
		
	@Type( base = Next.class )
	@XmlListBinding( path = "" )
	// Next vs terminating elements have to be in separate list becase the need
	// to be handled differently in diagram. (Next via connections).
	ListProperty PROP_NEXT_ELEMENTS = new ListProperty(TYPE, "NextElements");

	ElementList<Next> getNextElements();
	
	@Type( base = OutcomeElement.class,
			possible = {
				End.class,
				Fail.class,
				Stop.class
			}
	)
	@XmlListBinding( path = "" )
	// Next vs terminating elements have to be in separate list becase the need
	// to be handled differently in diagram. (Next via connections).
	ListProperty PROP_TERMINATING_ELEMENTS = new ListProperty(TYPE, "TerminatingElements");

	ElementList<OutcomeElement> getTerminatingElements();
	
	
}
