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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "decision" )
@Image ( path = "decision.png" )
@XmlBinding( path = "decision" )
public interface Decision extends FlowElement {

	ElementType TYPE = new ElementType( Decision.class );

	@Label( standard = "ref" )
	@XmlBinding( path = "@ref" )
	@Required

	ValueProperty PROP_REF = new ValueProperty( TYPE, "Ref" );

	Value<String> getRef();
	void setRef( String ref);

	@Type( base = Properties.class )
	@Label( standard = "properties" )
	@XmlBinding( path = "properties" )

	ElementProperty PROP_PROPERTIES = new ElementProperty( TYPE, "Properties" );

	ElementHandle<Properties> getProperties();

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
