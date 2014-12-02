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
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "stop" )
//@Image ( path = "stop.png" )
@XmlBinding( path = "stop" )

public interface Stop extends OutcomeElement {

	ElementType TYPE = new ElementType( Stop.class );

	@Label( standard = "exit-status" )
	@XmlBinding( path = "@exit-status" )

	ValueProperty PROP_EXIT_STATUS = new ValueProperty( TYPE, "ExitStatus" );

	Value<String> getExitStatus();
	void setExitStatus( String value);


	@Label( standard = "restart" )
	@XmlBinding( path = "@restart" )

	ValueProperty PROP_RESTART = new ValueProperty( TYPE, "Restart" );

	Value<String> getRestart();
	void setRestart( String value);

}
