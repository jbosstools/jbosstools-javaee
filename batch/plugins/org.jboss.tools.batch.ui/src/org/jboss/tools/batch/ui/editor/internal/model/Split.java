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
@Label( standard = "split" )
@Image ( path = "split.png" )
@XmlBinding( path = "split" )
public interface Split extends FlowElement {

	ElementType TYPE = new ElementType( Split.class );

	@Label( standard = "next" )
	@XmlBinding( path = "@next" )
	@Services ( {
		@Service( impl = NextPossibleValuesService.class )
	})

	ValueProperty PROP_NEXT = new ValueProperty( TYPE, "Next" );

	Value<String> getNext();
	void setNext( String next);

	@Type( base = Flow.class )
	@Label( standard = "flows" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "flow", type = Flow.class ) )

	ListProperty PROP_FLOWS = new ListProperty( TYPE, "Flows" );

	ElementList<Flow> getFlows();

}

