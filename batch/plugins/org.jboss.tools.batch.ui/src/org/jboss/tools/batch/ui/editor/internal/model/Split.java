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
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "split" )
@Image ( path = "split.png" )
@XmlBinding( path = "split" )
public interface Split extends FlowElement, NextAttributeElement {

	ElementType TYPE = new ElementType( Split.class );


	@Type( base = Flow.class )
	@Label( standard = "flows" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "flow", type = Flow.class ) )

	ListProperty PROP_FLOWS = new ListProperty( TYPE, "Flows" );

	ElementList<Flow> getFlows();
	
	@Type( base = Next.class )
	@XmlListBinding( path = "" )
	@Enablement( expr = "false" ) //TODO
	ListProperty PROP_NEXT_ELEMENTS = new ListProperty( TYPE, "NextElements" );

	ElementList<Next> getNextElements();

}

