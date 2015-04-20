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
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
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
public interface Job extends Element {

	ElementType TYPE = new ElementType( Job.class );

	@Label( standard = "id" )
	@XmlBinding( path = "@id" )

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

	ValueProperty PROP_RESTARTABLE = new ValueProperty( TYPE, "Restartable" );

	Value<String> getRestartable();
	void setRestartable( String restartable);


	@Type( base = Properties.class )
	@Label( standard = "properties" )
	@XmlBinding( path = "properties" )

	ElementProperty PROP_PROPERTIES = new ElementProperty( TYPE, "Properties" );

	ElementHandle<Properties> getProperties();

	@Type( base = Listener.class )
	@Label( standard = "listener" )
	@XmlListBinding( path = "listeners", mappings = @XmlListBinding.Mapping( element = "listener", type = Listener.class ))

	ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" ); //$NON-NLS-1$ 

	ElementList<Listener> getListeners();

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
	
}
