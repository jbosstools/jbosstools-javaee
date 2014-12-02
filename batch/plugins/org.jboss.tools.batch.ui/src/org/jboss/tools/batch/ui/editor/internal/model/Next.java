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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.jboss.tools.batch.ui.editor.internal.services.NextPossibleValuesService;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "next" )
//@Image ( path = "next.png" )
@XmlBinding( path = "next" )

public interface Next extends OutcomeElement {

	ElementType TYPE = new ElementType( Next.class );

	@Label( standard = "to" )
	@XmlBinding( path = "@to" )
	@Required
	@Services ( {
		@Service( impl = NextPossibleValuesService.class )
	})

	ValueProperty PROP_TO = new ValueProperty( TYPE, "To" );

	Value<String> getTo();
	void setTo( String value);

}
