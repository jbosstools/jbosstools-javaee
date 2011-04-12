/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.solder.core.test;


import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class VetoTest extends SeamSolderTest {

	public VetoTest() {}

	public void testVeto() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		//1. package annotated @Veto; class is not annotated with it
		TypeDefinition d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.vetoed.Tiger");
		assertNotNull(d);            //Though there exists Java type Tiger
		IAnnotationDeclaration a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		Object name = a.getMemberValue(null);
		assertEquals("tiger", name); //...and it is annotated with @Named("tiger")
		Set<IBean> bs = cdi.getBeans("tiger", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have a bean named "tiger"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//2. class annotated @Veto
		d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.somevetoed.Lion");
		assertNotNull(d);            //Though there exists Java type Lion
		a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		name = a.getMemberValue(null);
		assertEquals("lion", name);  //...and it is annotated with @Named("lion")
		bs = cdi.getBeans("lion", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have a bean named "lion"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource
	}

	public void testRequires() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);

		//1. class annotated @Requires that references single non-available class
		TypeDefinition d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.requires.Bear");
		assertNotNull(d);            //Though there exists Java type Bear
		IAnnotationDeclaration a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		Object name = a.getMemberValue(null);
		assertEquals("bear", name); //...and it is annotated with @Named("bear")
		Set<IBean> bs = cdi.getBeans("bear", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have a bean named "bear"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//2. class annotated @Requires that references array of classes some of which are not available
		d = cdi.getNature().getDefinitions().getTypeDefinition("org.jboss.requires.Bee");
		assertNotNull(d);            //Though there exists Java type Bee
		a = d.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		assertNotNull(a);
		name = a.getMemberValue(null);
		assertEquals("bee", name);  //...and it is annotated with @Named("bee")
		bs = cdi.getBeans("bee", false);
		assertTrue(bs.isEmpty());    //...CDI model does not have a bean named "bee"
		bs = cdi.getBeans(d.getResource().getFullPath());
		assertTrue(bs.isEmpty());    //...and does not loaded any beans form its resource

		//3. class annotated @Requires that references single available class
		bs = cdi.getBeans("fly", false);
		assertEquals(1, bs.size());    //...CDI model has a bean named "fly"

		//4. class annotated @Requires that references array of available classes
		bs = cdi.getBeans("dragonfly", false);
		assertEquals(1, bs.size());    //...CDI model has a bean named "dragonfly"

	}

}
