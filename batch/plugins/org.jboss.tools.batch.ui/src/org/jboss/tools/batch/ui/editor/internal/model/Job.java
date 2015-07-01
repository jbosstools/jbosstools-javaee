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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlNamespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlSchema;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@XmlNamespace( prefix = "", uri = "http://xmlns.jcp.org/xml/ns/javaee" )

@XmlSchema
(
    namespace = "http://xmlns.jcp.org/xml/ns/javaee",
    location = "http://xmlns.jcp.org/xml/ns/javaee/jobXML_1_0.xsd"
//    location = "file:///home/slava/JBossTools/sandbox/org.jboss.tools.jst.job.ui/schemas/jobXML_1_0.xsd"
)
@Image ( path = "job.png" )
@XmlBinding( path = "job" )
public interface Job extends Element, FlowElementsContainer {

	ElementType TYPE = new ElementType( Job.class );

	@Label( standard = "id" )
	@XmlBinding( path = "@id" )
	@Service(impl = IdValidationService.class)

	ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );

	Value<String> getId();
	void setId( String id);

	@Label( standard = "version" )
	@XmlBinding( path = "@version" )

	ValueProperty PROP_VERSION = new ValueProperty( TYPE, "Version" );

	Value<String> getVersion();
	void setVersion( String version);

	@Label( standard = "restartable" )
	@XmlBinding( path = "@restartable" )
	@Type(base = Boolean.class)
	@DefaultValue( text = "true" )
//	@PossibleValues(values = {"true", "false"})

	ValueProperty PROP_RESTARTABLE = new ValueProperty( TYPE, "Restartable" );

	Value<Boolean> getRestartable();
	void setRestartable( Boolean restartable);


	@Type( base = Properties.class )
	@Label( standard = "properties" )
	@XmlBinding( path = "properties" )

	ElementProperty PROP_PROPERTIES = new ElementProperty( TYPE, "Properties" );

	ElementHandle<Properties> getProperties();

	@Type( base = JobListener.class )
	@Label( standard = "listener" )
	@XmlListBinding( path = "listeners", mappings = @XmlListBinding.Mapping( element = "listener", type = JobListener.class ))

	ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" ); //$NON-NLS-1$ 

	ElementList<JobListener> getListeners();
	
}
