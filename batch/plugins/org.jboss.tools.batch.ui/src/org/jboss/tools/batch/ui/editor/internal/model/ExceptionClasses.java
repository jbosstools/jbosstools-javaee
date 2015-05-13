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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface ExceptionClasses extends Element {
	
	ElementType TYPE = new ElementType( ExceptionClasses.class );
    
	@Type( base = ClassElement.class )
	@Label( standard = "includes" )
	@XmlListBinding( path = "", mappings = @XmlListBinding.Mapping( element = "include", type = ClassElement.class ))

	ListProperty PROP_INCLUDES = new ListProperty( TYPE, "Includes" ); //$NON-NLS-1$ 

	ElementList<ClassElement> getIncludes();

	@Type( base = ClassElement.class )
	@Label( standard = "excludes" )
	@XmlListBinding( path = "", mappings = @XmlListBinding.Mapping( element = "exclude", type = ClassElement.class ))

	ListProperty PROP_EXCLUDES = new ListProperty( TYPE, "Excludes" ); //$NON-NLS-1$ 

	ElementList<ClassElement> getExcludes();
	
}
