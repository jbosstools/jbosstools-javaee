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
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.jboss.tools.batch.ui.editor.internal.services.NextPossibleValuesService;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "flow" )
//@Image ( path = "flow.png" )
@XmlBinding( path = "flow" )
public interface Flow extends FlowElement {
	
	ElementType TYPE = new ElementType( Flow.class );
    
    @Label( standard = "next" )
    @XmlBinding( path = "@next" )
	@Services ( {
		@Service( impl = NextPossibleValuesService.class )
	})
    
    ValueProperty PROP_NEXT = new ValueProperty( TYPE, "Next" );
	
    Value<String> getNext();
    void setNext( String next);
	
	@Type( base = FlowElement.class,
			possible = {
				Flow.class,
				Step.class,
				Decision.class,
				Split.class
			}
	)
	@XmlListBinding( path = "" )
	ListProperty PROP_FLOW_ELEMENTS = new ListProperty(TYPE, "FlowElements");

	ElementList<FlowElement> getFlowElements();
	
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
	
}
