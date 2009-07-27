/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.seam.test.unit;

import java.io.InputStreamReader;
import java.io.Reader;

import org.jboss.seam.text.xpl.SeamTextLexer;
import org.jboss.seam.text.xpl.SeamTextParser;

/**
 * @author Vitali (vyemialyanchyk@exadel.com)
 *
 * Test seam lexer functionality 
 */
public class SeamTextTest
{
    public static void main(String[] args) throws Exception {
        Reader r = new InputStreamReader( SeamTextTest.class.getResourceAsStream("SeamTextTest.txt") ); //$NON-NLS-1$
        SeamTextLexer lexer = new SeamTextLexer(r);
        SeamTextParser parser = new SeamTextParser(lexer);
        parser.startRule();
        System.out.println(parser);
    }
}
