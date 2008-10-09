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
import org.jboss.tools.vpe.ui.test.VpeTest;

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
	public void testJBIDE2828JbossELParser() {
		
		ELParserFactory jbossParserFactory = ELParserUtil.getJbossFactory();
		
		assertNotNull("Couldn't get jboss parser factory", jbossParserFactory); //$NON-NLS-1$
		
		ELParser elParser = jbossParserFactory.createParser();
		
		assertNotNull(elParser);
		
		ELModel elModel1 =  elParser.parse("#{faces.context}"); //$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel2 =  elParser.parse("faces.context"); //$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel3 =  elParser.parse("");//$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel4 = elParser.parse("org.richfaces.SKIN"); //$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel5 = elParser.parse(" jdsfh dskfj lksdjf asjfdsd; dsf; ");//$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		assertNotNull(elModel1);
	}
	/**
	 * Test for default parser
	 */
	public void testJBIDE2828DefaultELParser() {
		
		ELParserFactory defaultParserFactory = ELParserUtil.getDefaultFactory();
		
		assertNotNull("Couldn't get jboss parser factory", defaultParserFactory); //$NON-NLS-1$
		
		ELParser elParser = defaultParserFactory.createParser();
		
		assertNotNull(elParser);
		
		ELModel elModel1 =  elParser.parse("#{faces.context}"); //$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel2 =  elParser.parse("faces.context"); //$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel3 =  elParser.parse("");//$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel4 = elParser.parse("org.richfaces.SKIN"); //$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		ELModel elModel5 = elParser.parse(" jdsfh dskfj lksdjf asjfdsd; dsf; ");//$NON-NLS-1$
		assertEquals("In which expression parser will found error?",elParser.getSyntaxErrors().size(),0); //$NON-NLS-1$
		assertNotNull(elModel1);
	}
	
}
