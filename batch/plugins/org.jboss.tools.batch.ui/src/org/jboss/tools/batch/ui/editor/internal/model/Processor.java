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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.annotations.Image;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Image ( path = "processor.png" )
public interface Processor extends ItemHandlingElement, RefAttributeElement {
	ElementType TYPE = new ElementType( Processor.class );
}
