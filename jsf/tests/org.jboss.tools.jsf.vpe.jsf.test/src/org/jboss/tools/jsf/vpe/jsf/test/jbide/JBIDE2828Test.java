/*******************************************************************************
* Copyright (c) 2007-2008 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributor:
*     Red Hat, Inc. - initial API and implementation
******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.parser.ELParser;
import org.jboss.tools.common.el.core.parser.ELParserFactory;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * Test case for jbide-2828
 * 
 * @author mareshkau
 *
 */
public class JBIDE2828Test extends VpeTest {
	/**
	 * 
	 * @param name
	 */
	public JBIDE2828Test(String name) {
		super(name);
	}
	public void testJBIDE2828JbossELParser() throws Throwable {
		
		setException(null);
		
		ELParserFactory jbossParserFactory = ELParserUtil.getJbossFactory();
		
		assertNotNull("Couldn't get jboss parser factory", jbossParserFactory); //$NON-NLS-1$
		
		ELParser elParser = jbossParserFactory.createParser();
		
		assertNotNull(elParser);
		
		checkELParser(elParser);
		
		if(getException()!=null) {
			
			throw getException();
		}
	}
	/**
	 * Test for default parser
	 */
	public void testJBIDE2828DefaultELParser() throws Throwable {
		
		setException(null);
		
		ELParserFactory defaultParserFactory = ELParserUtil.getDefaultFactory();
		
		assertNotNull("Couldn't get jboss parser factory", defaultParserFactory); //$NON-NLS-1$
		
		ELParser elParser = defaultParserFactory.createParser();
		
		assertNotNull(elParser);
		
		checkELParser(elParser);
		
		if(getException()!=null) {
			
			throw getException();
		}
	}
	/**
	 * Checks el parser
	 */	
	private void checkELParser(ELParser elParser) {
		ELModel elModel1 =  elParser.parse("#{faces.context}"); //$NON-NLS-1$
		assertNotNull(elModel1);
		assertEquals("There shouldn't be errors",elModel1.getSyntaxErrors().size(),0); //$NON-NLS-1$
		
		ELModel elModel2 = elParser.parse("#{org.richfaces.SKIN}"); //$NON-NLS-1$
		assertNotNull(elModel2);
		assertEquals("There shouldn't be errors",elModel2.getSyntaxErrors().size(),0); //$NON-NLS-1$
	
		ELModel elModel3 = elParser.parse("#{klsjdf lsaijf aslkjd; sikjfd}"); //$NON-NLS-1$
		assertNotNull(elModel3);
		assertTrue("There should be errorrs", elModel3.getSyntaxErrors().size()>0); //$NON-NLS-1$
	
	}
	
}
